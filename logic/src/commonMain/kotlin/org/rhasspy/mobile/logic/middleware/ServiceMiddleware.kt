package org.rhasspy.mobile.logic.middleware

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okio.Path
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.AppSettingsServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.StopListening
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.WakeWordDetected
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceState.*
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.settings.AppSettingsService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting

/**
 * handles ALL INCOMING events
 */
class ServiceMiddleware(
    private val dialogManagerService: DialogManagerService,
    private val speechToTextService: SpeechToTextService,
    private val textToSpeechService: TextToSpeechService,
    private val appSettingsService: AppSettingsService,
    private val localAudioService: LocalAudioService,
    private val mqttService: MqttService,
    private val wakeWordService: WakeWordService
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _isPlayingRecording = MutableStateFlow(false)
    val isPlayingRecording = _isPlayingRecording.readOnly
    val isPlayingRecordingEnabled = combineState(_isPlayingRecording, dialogManagerService.currentDialogState) { playing, state ->
        playing || (state == Idle || state == AwaitingWakeWord)
    }

    val isUserActionEnabled = combineState(_isPlayingRecording, dialogManagerService.currentDialogState) { playingRecording, dialogState ->
        !playingRecording && (dialogState == Idle || dialogState == AwaitingWakeWord || dialogState == RecordingIntent)
    }
    private var shouldResumeHotWordService = false

    fun action(serviceMiddlewareAction: ServiceMiddlewareAction) {
        coroutineScope.launch {
            when (serviceMiddlewareAction) {
                is PlayStopRecording -> {
                    if (_isPlayingRecording.value) {
                        _isPlayingRecording.value = false
                        if (shouldResumeHotWordService) {
                            action(HotWordToggle(true))
                        }
                        action(DialogServiceMiddlewareAction.PlayFinished(Source.Local))
                    } else {
                        if (dialogManagerService.currentDialogState.value == Idle ||
                            dialogManagerService.currentDialogState.value == AwaitingWakeWord
                        ) {
                            _isPlayingRecording.value = true
                            shouldResumeHotWordService = AppSetting.isHotWordEnabled.value
                            action(HotWordToggle(false))
                            //suspend coroutine
                            localAudioService.playAudio(AudioSource.File(speechToTextService.speechToTextAudioFile))
                            //resumes when play finished
                            if (_isPlayingRecording.value) {
                                action(PlayStopRecording)
                            }
                        }
                    }
                }

                is WakeWordError -> mqttService.wakeWordError(serviceMiddlewareAction.description)
                is AppSettingsServiceMiddlewareAction -> {
                    when (serviceMiddlewareAction) {
                        is AudioOutputToggle -> appSettingsService.audioOutputToggle(serviceMiddlewareAction.enabled)
                        is AudioVolumeChange -> appSettingsService.setAudioVolume(serviceMiddlewareAction.volume)

                        is HotWordToggle -> {
                            appSettingsService.hotWordToggle(serviceMiddlewareAction.enabled)
                            if (serviceMiddlewareAction.enabled) {
                                wakeWordService.startDetection()
                            } else {
                                wakeWordService.stopDetection()
                            }
                        }

                        is IntentHandlingToggle -> appSettingsService.intentHandlingToggle(serviceMiddlewareAction.enabled)
                    }
                }

                is SayText -> textToSpeechService.textToSpeech("", serviceMiddlewareAction.text)
                is DialogServiceMiddlewareAction -> dialogManagerService.onAction(serviceMiddlewareAction)
                is Mqtt -> mqttService.onMessageReceived(serviceMiddlewareAction.topic, serviceMiddlewareAction.payload)
            }
        }
    }

    fun userSessionClick() {
        when (dialogManagerService.currentDialogState.value) {
            AwaitingWakeWord -> action(WakeWordDetected(Source.Local, "manual"))
            RecordingIntent -> action(StopListening(Source.Local))
            else -> {}
        }
    }

    fun getRecordedFile(): Path = speechToTextService.speechToTextAudioFile

}