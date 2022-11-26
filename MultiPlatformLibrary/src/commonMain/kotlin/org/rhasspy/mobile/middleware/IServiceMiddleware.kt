package org.rhasspy.mobile.middleware

import org.rhasspy.mobile.middleware.action.LocalAction
import org.rhasspy.mobile.middleware.action.MqttAction
import org.rhasspy.mobile.middleware.action.WebServerAction
import org.rhasspy.mobile.middleware.action.WebServerRequest

/**
 * handles ALL INCOMING events
 */
abstract class IServiceMiddleware {

    /**
     * user clicks start or hotword was detected
     */
    fun localAction(event: LocalAction) {

    }

    fun mqttAction(event: MqttAction) {

    }

    fun webServerAction(event: WebServerAction) {

    }

    fun <T> webServerRequest(event: WebServerRequest<T>): T {
        TODO()
    }


    fun httpAction() {

    }

    fun createEvent(eventType: EventType, description: String? = null): Event {

        /**
         * eventually when testing update an existing(pending) event with event type
         */

        return Event(eventType, description).loading()
    }

}