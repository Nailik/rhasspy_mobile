package org.rhasspy.mobile.nativeutils

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.mqtt.MqttError
import org.rhasspy.mobile.mqtt.MqttMessage
import org.rhasspy.mobile.mqtt.MqttPersistence
import org.rhasspy.mobile.mqtt.MqttQos
import org.rhasspy.mobile.services.mqtt.MqttServiceConnectionOptions

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
     * Connects to the MQTT Broker.
     * @param connOptions The connection options to use.
     * @return Will return a [error][MqttError] if a problem has occurred.
     */
    actual suspend fun connect(connOptions: MqttServiceConnectionOptions): MqttError? {
        TODO("Not yet implemented")
    }

    /**
     * Disconnects from the MQTT Broker.
     * @return Will return a [error][MqttError] if a problem has occurred.
     */
    actual fun disconnect(): MqttError? {
        TODO("Not yet implemented")
    }

}