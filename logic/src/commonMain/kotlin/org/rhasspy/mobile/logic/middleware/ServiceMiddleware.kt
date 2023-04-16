package org.rhasspy.mobile.logic.middleware

import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceState
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.settings.AppSettingsService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.readOnly

/**
 * handles ALL INCOMING events
 */
class ServiceMiddleware : KoinComponent, Closeable {

    private val dialogManagerService by inject<DialogManagerService>()
    private val speechToTextService by inject<SpeechToTextService>()
    private val appSettingsService by inject<AppSettingsService>()
    private val localAudioService by inject<LocalAudioService>()
    private val mqttService by inject<MqttService>()
    private val wakeWordService by inject<WakeWordService>()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _isPlayingRecording = MutableStateFlow(false)
    val isPlayingRecording = _isPlayingRecording.readOnly
    val isPlayingRecordingEnabled = combineState(_isPlayingRecording, dialogManagerService.currentDialogState) { playing, state ->
        playing || (state == DialogManagerServiceState.Idle || state == DialogManagerServiceState.AwaitingWakeWord)
    }

    val isUserActionEnabled = combineState(_isPlayingRecording, dialogManagerService.currentDialogState) { playingRecording, dialogState ->
        !playingRecording && (dialogState == DialogManagerServiceState.Idle ||
                dialogState == DialogManagerServiceState.AwaitingWakeWord ||
                dialogState == DialogManagerServiceState.RecordingIntent)
    }
    private var shouldResumeHotWordService = false

    fun action(serviceMiddlewareAction: ServiceMiddlewareAction) {
        coroutineScope.launch {
            when (serviceMiddlewareAction) {
                is ServiceMiddlewareAction.PlayStopRecording -> {
                    if (_isPlayingRecording.value) {
                        _isPlayingRecording.value = false
                        if (shouldResumeHotWordService) {
                            action(ServiceMiddlewareAction.AppSettingsServiceMiddlewareAction.HotWordToggle(true))
                        }
                        action(ServiceMiddlewareAction.DialogServiceMiddlewareAction.PlayFinished(Source.Local))
                    } else {
                        if (dialogManagerService.currentDialogState.value == DialogManagerServiceState.Idle ||
                            dialogManagerService.currentDialogState.value == DialogManagerServiceState.AwaitingWakeWord
                        ) {
                            _isPlayingRecording.value = true
                            shouldResumeHotWordService = AppSetting.isHotWordEnabled.value
                            action(ServiceMiddlewareAction.AppSettingsServiceMiddlewareAction.HotWordToggle(false))
                            //suspend coroutine
                            localAudioService.playAudio(AudioSource.File(speechToTextService.speechToTextAudioFile))
                            //resumes when play finished
                            if (_isPlayingRecording.value) {
                                action(ServiceMiddlewareAction.PlayStopRecording)
                            }
                        }
                    }
                }

                is ServiceMiddlewareAction.WakeWordError -> mqttService.wakeWordError(serviceMiddlewareAction.description)
                is ServiceMiddlewareAction.AppSettingsServiceMiddlewareAction -> {
                    when (serviceMiddlewareAction) {
                        is ServiceMiddlewareAction.AppSettingsServiceMiddlewareAction.AudioOutputToggle ->
                            appSettingsService.audioOutputToggle(serviceMiddlewareAction.enabled)

                        is ServiceMiddlewareAction.AppSettingsServiceMiddlewareAction.AudioVolumeChange ->
                            appSettingsService.setAudioVolume(serviceMiddlewareAction.volume)

                        is ServiceMiddlewareAction.AppSettingsServiceMiddlewareAction.HotWordToggle -> {
                            appSettingsService.hotWordToggle(serviceMiddlewareAction.enabled)
                            if (serviceMiddlewareAction.enabled) {
                                wakeWordService.startDetection()
                            } else {
                                wakeWordService.stopDetection()
                            }
                        }

                        is ServiceMiddlewareAction.AppSettingsServiceMiddlewareAction.IntentHandlingToggle ->
                            appSettingsService.intentHandlingToggle(serviceMiddlewareAction.enabled)
                    }
                }

                is ServiceMiddlewareAction.SayText -> {
                    get<TextToSpeechService>().textToSpeech("", serviceMiddlewareAction.text)
                }

                is ServiceMiddlewareAction.DialogServiceMiddlewareAction -> {
                    dialogManagerService.onAction(serviceMiddlewareAction)
                }

                is ServiceMiddlewareAction.Mqtt -> mqttService.onMessageReceived(serviceMiddlewareAction.topic, serviceMiddlewareAction.payload)
            }
        }
    }

    fun userSessionClick() {
        when (dialogManagerService.currentDialogState.value) {
            DialogManagerServiceState.AwaitingWakeWord -> {
                action(ServiceMiddlewareAction.DialogServiceMiddlewareAction.WakeWordDetected(Source.Local, "manual"))
            }

            DialogManagerServiceState.RecordingIntent -> {
                action(ServiceMiddlewareAction.DialogServiceMiddlewareAction.StopListening(Source.Local))
            }

            else -> {}
        }
    }

    fun getRecordedFile(): Path = speechToTextService.speechToTextAudioFile

    override fun close() {
        coroutineScope.cancel()
    }

}