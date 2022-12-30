package org.rhasspy.mobile.middleware

import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.dialogManager.*
import org.rhasspy.mobile.services.localaudio.LocalAudioService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.recording.RecordingService
import org.rhasspy.mobile.services.settings.AppSettingsService

/**
 * handles ALL INCOMING events
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class IServiceMiddleware : KoinComponent, Closeable {

    //replay because maybe the test starts a little bit earlier than subscription to the shared flow
    private val _event = MutableSharedFlow<Event>(
        replay = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event = _event.readOnly

    private val dialogManagerService by inject<DialogManagerService>()
    private val appSettingsService by inject<AppSettingsService>()
    private val recordingService by inject<RecordingService>()
    private val localAudioService by inject<LocalAudioService>()
    private val mqttService by inject<MqttService>()
    val coroutineScope = CoroutineScope(Dispatchers.Default)


    fun action(action: Action) {
        coroutineScope.launch {
            when (action) {
                is Action.PlayRecording -> localAudioService.playAudio(recordingService.recordedData)
                is Action.WakeWordError -> mqttService.wakeWordError(action.description)
                is Action.AppSettingsAction -> {
                    when (action) {
                        is Action.AppSettingsAction.AudioOutputToggle -> appSettingsService.audioOutputToggle(action.enabled)
                        is Action.AppSettingsAction.AudioVolumeChange -> appSettingsService.setAudioVolume(action.volume)
                        is Action.AppSettingsAction.HotWordToggle -> appSettingsService.hotWordToggle(action.enabled)
                        is Action.AppSettingsAction.IntentHandlingToggle -> appSettingsService.intentHandlingToggle(action.enabled)
                    }
                }
                is Action.DialogAction -> {                //change settings
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

    fun getRecordedData(): ByteArray = recordingService.recordedData.toByteArray()

    /**
     * eventually when testing update an existing(pending) event with event type
     */
    fun createEvent(eventType: EventType, description: String? = null): Event {
        val event = Event(eventType, description).loading()
        _event.tryEmit(event)
        return event
    }

    override fun close() {
        coroutineScope.cancel()
        _event.resetReplayCache()
    }

}