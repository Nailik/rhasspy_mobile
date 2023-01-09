package org.rhasspy.mobile.middleware

import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.services.dialog.DialogManagerService
import org.rhasspy.mobile.services.dialog.DialogManagerServiceState
import org.rhasspy.mobile.services.localaudio.LocalAudioService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.settings.AppSettingsService
import org.rhasspy.mobile.services.speechtotext.SpeechToTextService

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


    fun action(action: Action) {
        coroutineScope.launch {
            when (action) {
                is Action.PlayStopRecording -> {
                    if (localAudioService.isPlayingState.value) {
                        localAudioService.stop()
                    } else {
                        localAudioService.playAudio(speechToTextService.speechToTextAudioData)
                    }
                }
                is Action.WakeWordError -> mqttService.wakeWordError(action.description)
                is Action.AppSettingsAction -> {
                    when (action) {
                        is Action.AppSettingsAction.AudioOutputToggle -> appSettingsService.audioOutputToggle(action.enabled)
                        is Action.AppSettingsAction.AudioVolumeChange -> appSettingsService.setAudioVolume(action.volume)
                        is Action.AppSettingsAction.HotWordToggle -> appSettingsService.hotWordToggle(action.enabled)
                        is Action.AppSettingsAction.IntentHandlingToggle -> appSettingsService.intentHandlingToggle(action.enabled)
                    }
                }
                is Action.DialogAction -> {
                    dialogManagerService.onAction(action)
                }
            }
        }
    }

    fun userSessionClick() {
        if (dialogManagerService.currentDialogState.value == DialogManagerServiceState.AwaitingWakeWord) {
            action(Action.DialogAction.WakeWordDetected(Source.Local, "manual"))
        } else {
            action(Action.DialogAction.StopListening(Source.Local))
        }
    }

    fun getRecordedData(): ByteArray = speechToTextService.speechToTextAudioData.toByteArray()

    override fun close() {
        coroutineScope.cancel()
    }

}