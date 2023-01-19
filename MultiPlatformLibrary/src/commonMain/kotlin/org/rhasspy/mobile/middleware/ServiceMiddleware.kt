package org.rhasspy.mobile.middleware

import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.nativeutils.FileWavStream
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.dialog.DialogManagerService
import org.rhasspy.mobile.services.dialog.DialogManagerServiceState
import org.rhasspy.mobile.services.localaudio.LocalAudioService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.settings.AppSettingsService
import org.rhasspy.mobile.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.settings.AppSetting

/**
 * handles ALL INCOMING events
 */
class ServiceMiddleware : KoinComponent, Closeable {

    private val dialogManagerService by inject<DialogManagerService>()
    private val speechToTextService by inject<SpeechToTextService>()
    private val appSettingsService by inject<AppSettingsService>()
    private val localAudioService by inject<LocalAudioService>()
    private val mqttService by inject<MqttService>()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _isPlayingRecording = MutableStateFlow(false)
    val isPlayingRecording = _isPlayingRecording.readOnly
    val isPlayingRecordingEnabled = combineState(_isPlayingRecording, dialogManagerService.currentDialogState) { playing, state ->
        playing || (state == DialogManagerServiceState.Idle || state == DialogManagerServiceState.AwaitingWakeWord)
    }

    private var shouldResumeHotWordService = false

    fun action(action: Action) {
        coroutineScope.launch {
            when (action) {
                is Action.PlayStopRecording -> {
                    if (_isPlayingRecording.value) {
                        _isPlayingRecording.value = false
                        if (shouldResumeHotWordService) {
                            appSettingsService.hotWordToggle(true)
                        }
                        action(Action.DialogAction.PlayFinished(Source.Local))
                    } else {
                        if (dialogManagerService.currentDialogState.value == DialogManagerServiceState.Idle ||
                            dialogManagerService.currentDialogState.value == DialogManagerServiceState.AwaitingWakeWord &&
                            speechToTextService.getSpeechToTextAudioStream().length > 0
                        ) {
                            _isPlayingRecording.value = true
                            shouldResumeHotWordService = AppSetting.isHotWordEnabled.value
                            appSettingsService.hotWordToggle(false)
                            //suspend coroutine
                            localAudioService.playAudio(speechToTextService.getSpeechToTextAudioStream())
                            //resumes when play finished
                            if (_isPlayingRecording.value) {
                                action(Action.PlayStopRecording)
                            }
                        }
                    }
                }
                is Action.WakeWordError -> mqttService.wakeWordError(action.description)
                is Action.AppSettingsAction -> {
                    when (action) {
                        is Action.AppSettingsAction.AudioOutputToggle -> appSettingsService.audioOutputToggle(
                            action.enabled
                        )
                        is Action.AppSettingsAction.AudioVolumeChange -> appSettingsService.setAudioVolume(
                            action.volume
                        )
                        is Action.AppSettingsAction.HotWordToggle -> appSettingsService.hotWordToggle(
                            action.enabled
                        )
                        is Action.AppSettingsAction.IntentHandlingToggle -> appSettingsService.intentHandlingToggle(
                            action.enabled
                        )
                    }
                }
                is Action.DialogAction -> {
                    dialogManagerService.onAction(action)
                }
            }
        }
    }

    fun userSessionClick() {
        when (dialogManagerService.currentDialogState.value) {
            DialogManagerServiceState.AwaitingWakeWord -> {
                action(Action.DialogAction.WakeWordDetected(Source.Local, "manual"))
            }
            DialogManagerServiceState.RecordingIntent -> {
                action(Action.DialogAction.StopListening(Source.Local))
            }
            else -> {}
        }
    }

    fun getRecordedData(): FileWavStream = speechToTextService.getSpeechToTextAudioStream()

    override fun close() {
        coroutineScope.cancel()
    }

}