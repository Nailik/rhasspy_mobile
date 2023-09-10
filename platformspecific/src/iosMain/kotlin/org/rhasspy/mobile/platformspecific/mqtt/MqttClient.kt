package org.rhasspy.mobile.platformspecific.mqtt

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.mqtt.MqttServiceConnectionOptions

actual class MqttClient actual constructor(
    brokerUrl: String,
    clientId: String,
    persistenceType: MqttPersistence,
    onDelivered: (token: Int) -> Unit,
    onMessageReceived: (topic: String, message: MqttMessage) -> Unit,
    onDisconnect: (error: Throwable) -> Unit
) {
    /** If *true* then there is a connection to the MQTT Broker. */
    actual val isConnected: StateFlow<Boolean>
        get() = MutableStateFlow(false) //TODO #261

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
        //TODO #261
        return null
    }

    /**
     * Subscribes to a topic.
     * @param topic The MQTT topic to use.
     * @param qos The MQTT quality of service to use.
     * @return Will return a [error][MqttError] if a problem has occurred.
     */
    actual suspend fun subscribe(topic: String, qos: MqttQos): MqttError? {
        //TODO #261
        return null
    }

    /**
     * Connects to the MQTT Broker.
     * @param connOptions The connection options to use.
     * @return Will return a [error][MqttError] if a problem has occurred.
     */
    actual suspend fun connect(connOptions: MqttServiceConnectionOptions): MqttError? {
        //TODO #261
        return null
    }

    /**
     * Disconnects from the MQTT Broker.
     * @return Will return a [error][MqttError] if a problem has occurred.
     */
    actual fun disconnect(): MqttError? {
        //TODO #261
        return null
    }

}