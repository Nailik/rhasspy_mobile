package org.rhasspy.mobile.middleware

import io.ktor.utils.io.core.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.rhasspy.mobile.middleware.action.LocalAction
import org.rhasspy.mobile.middleware.action.MqttAction
import org.rhasspy.mobile.middleware.action.WebServerAction
import org.rhasspy.mobile.middleware.action.WebServerRequest
import org.rhasspy.mobile.readOnly

/**
 * handles ALL INCOMING events
 */
abstract class IServiceMiddleware : Closeable {

    //replay because maybe the test starts a little bit earlier than subscription to the shared flow
    private val _event = MutableSharedFlow<Event>(
        replay = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event = _event.readOnly


    /**
     * user clicks start or hotword was detected
     */
    fun localAction(event: LocalAction) {

    }

    fun mqttAction(event: MqttAction) {
        //post action to the service that needs it
        // -> asr (stop, start, text captured) is needed by rhasspy actions
        //intent recognized, intent not recognized is needed by rhasspy actions

        //TODO split rhasspy actions service into speechtotext, texttospeech, intent recognition
    }

    fun webServerAction(event: WebServerAction) {

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
        _event.resetReplayCache()
    }


}