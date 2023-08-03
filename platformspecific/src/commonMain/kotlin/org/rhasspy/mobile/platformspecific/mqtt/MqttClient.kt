package org.rhasspy.mobile.platformspecific.mqtt

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.mqtt.MqttServiceConnectionOptions

/**
 * Represents a MQTT client which can connect to the MQTT Broker.
 */
expect class MqttClient(
    //The MQTT Broker address which is comprised of a protocol, IPv4 address/FQDN, and port. Here is a example:`tcp://192.168.1.1:1883`
    brokerUrl: String,
    //unique MQTT client identifier.
    clientId: String,
    persistenceType: MqttPersistence = MqttPersistence.NONE,
    //Handles the deliveryComplete event. First argument is the delivery token.
    onDelivered: (token: Int) -> Unit,
    //Handles the messageArrived event. First argument is the topic. Second argument is the [message][MqttMessage].
    onMessageReceived: (topic: String, message: MqttMessage) -> Unit,
    //Handles the connectionLost event. First argument is the cause.
    onDisconnect: (error: Throwable) -> Unit
) {
    /**
     * If *true* then there is a connection to the MQTT Broker.
     **/
    val isConnected: StateFlow<Boolean>

    /**
     * Publishes a message to the MQTT Broker.
     * @param topic The MQTT topic to use.
     * @param msg The MQTT message which includes the payload.
     * @param timeout Timeout for publishing in milliseconds.
     * @return Will return a [error][MqttError] if a problem has occurred.
     *
     * Handles the deliveryComplete event. First argument is the delivery token.
     */
    suspend fun publish(topic: String, msg: MqttMessage, timeout: Long = 2000L): MqttError?

    /**
     * Subscribes to a topic.
     * @param topic The MQTT topic to use.
     * @param qos The MQTT quality of service to use.
     * @return Will return a [error][MqttError] if a problem has occurred.
     */
    suspend fun subscribe(topic: String, qos: MqttQos = MqttQos.AT_MOST_ONCE): MqttError?

    /**
     * Connects to the MQTT Broker.
     * @param connOptions The connection options to use.
     * @return Will return a [error][MqttError] if a problem has occurred.
     */
    suspend fun connect(connOptions: MqttServiceConnectionOptions): MqttError?

    /**
     * Disconnects from the MQTT Broker.
     * @return Will return a [error][MqttError] if a problem has occurred.
     */
    fun disconnect(): MqttError?
}