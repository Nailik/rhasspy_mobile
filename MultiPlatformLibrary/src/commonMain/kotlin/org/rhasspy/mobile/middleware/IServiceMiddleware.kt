package org.rhasspy.mobile.middleware

import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.middleware.action.LocalAction
import org.rhasspy.mobile.middleware.action.MqttAction
import org.rhasspy.mobile.middleware.action.WebServerAction
import org.rhasspy.mobile.middleware.action.WebServerRequest
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.dialogManager.IDialogManagerService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService

/**
 * handles ALL INCOMING events
 */
abstract class IServiceMiddleware : KoinComponent, Closeable {

    //replay because maybe the test starts a little bit earlier than subscription to the shared flow
    private val _event = MutableSharedFlow<Event>(
        replay = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event = _event.readOnly

    private val _serviceErrors = MutableSharedFlow<EventState.Error>()
    val serviceErrors = _serviceErrors.readOnly

    val sessionId: String get() = get<IDialogManagerService>().sessionId

    val rhasspyActionsService by inject<RhasspyActionsService>()
    val coroutineScope = CoroutineScope(Dispatchers.Default)


    /**
     * user clicks start or hotword was detected
     */
    fun localAction(event: LocalAction) {

    }

    open fun mqttAction(event: MqttAction) {
        //post action to the service that needs it
        // -> asr (stop, start, text captured) is needed by rhasspy actions
        //intent recognized, intent not recognized is needed by rhasspy actions

        coroutineScope.launch {
            //TODO split rhasspy actions service into speechtotext, texttospeech, intent recognition
            when (event) {
                is MqttAction.AudioOutputToggle -> {}// TODO()
                is MqttAction.AudioVolumeChange -> {}// TODO()
                is MqttAction.EndSession -> {}// TODO()
                is MqttAction.HotWordDetected -> {}// TODO()
                is MqttAction.HotWordToggle -> {}// TODO()
                is MqttAction.IntentHandlingToggle -> {}// TODO()
                is MqttAction.IntentRecognitionResult -> {}// TODO()
                is MqttAction.AsrError -> {}
                //TODO only if mqtt is used?? for speech to text and session id is correct (but for dialog manager mqtt any)
                is MqttAction.AsrTextCaptured -> {}
                //TODO only if mqtt is used?? for speech to text and session id is correct (but for dialog manager mqtt any)
                is MqttAction.IntentTranscribed -> {}// TODO()
                is MqttAction.IntentTranscribedError -> {}// TODO()
                is MqttAction.PlayAudio -> {}// TODO()
                is MqttAction.SessionEnded -> {}// TODO()
                is MqttAction.SessionStarted -> {}// TODO()
                is MqttAction.StartListening -> {}// TODO()
                MqttAction.StartSession -> {}// TODO()
                is MqttAction.StopListening -> {}// TODO()
            }
        }

    }

    open fun webServerAction(event: WebServerAction) {

    }

    fun <T> webServerRequest(event: WebServerRequest<T>): T {
        TODO()
    }

    /**
     * eventually when testing update an existing(pending) event with event type
     */
    fun createEvent(eventType: EventType, description: String? = null): Event {
        val event = Event(eventType, description).loading()
        _event.tryEmit(event)
        return event
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun close() {
        coroutineScope.cancel()
        _event.resetReplayCache()
    }

    fun silenceDetected() {
        //TODO("Not yet implemented")
    }


}