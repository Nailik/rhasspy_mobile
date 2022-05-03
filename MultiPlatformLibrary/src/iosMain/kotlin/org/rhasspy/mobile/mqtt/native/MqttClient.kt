package org.rhasspy.mobile.mqtt.native

import org.rhasspy.mobile.mqtt.*

actual class MqttClient actual constructor(
    brokerUrl: String,
    clientId: String,
    persistenceType: MqttPersistence,
    onDelivered: (token: Int) -> Unit,
    onMessageReceived: (topic: String, message: MqttMessage) -> Unit,
    onDisconnect: (error: Throwable) -> Unit
) {
    /** If *true* then there is a connection to the MQTT Broker. */
    actual val isConnected: Boolean
        get() = TODO("Not yet implemented")

    /**
     * Publishes a message to the MQTT Broker.
     * @param topic The MQTT topic to use.
     * @param msg The MQTT message which includes the payload.
     * @param timeout Timeout for publishing in milliseconds.
     * @return Will return a [error][MqttError] if a problem has occurred.
     *
     * Handles the deliveryComplete event. First argument is the delivery token.
     */
    actual suspend fun publish(
        topic: String,
        msg: MqttMessage,
        timeout: Long
    ): MqttError? {
        TODO("Not yet implemented")
    }

    /**
     * Subscribes to a topic.
     * @param topic The MQTT topic to use.
     * @param qos The MQTT quality of service to use.
     * @return Will return a [error][MqttError] if a problem has occurred.
     */
    actual suspend fun subscribe(topic: String, qos: MqttQos): MqttError? {
        TODO("Not yet implemented")
    }

    /**
     * Will unsubscribe from one or more topics.
     * @param topics One or more topics to unsubscribe from. Can include topic filter(s).
     * @return Will return a error if a problem has occurred.
     */
    @Suppress("unused")
    actual suspend fun unsubscribe(vararg topics: String): MqttError? {
        TODO("Not yet implemented")
    }

    /**
     * Connects to the MQTT Broker.
     * @param connOptions The connection options to use.
     * @return Will return a [error][MqttError] if a problem has occurred.
     */
    actual suspend fun connect(connOptions: MqttConnectionOptions): MqttError? {
        TODO("Not yet implemented")
    }

    /**
     * Disconnects from the MQTT Broker.
     * @return Will return a [error][MqttError] if a problem has occurred.
     */
    actual suspend fun disconnect(): MqttError? {
        TODO("Not yet implemented")
    }

}