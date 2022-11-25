package org.rhasspy.mobile.services.middleware

interface IServiceMiddleware {

    /**
     * user clicks start or hotword was detected
     */
    fun localEvent(event: LocalEvent) {

    }

    fun mqttEvent(event: MqttEvent){

    }

}