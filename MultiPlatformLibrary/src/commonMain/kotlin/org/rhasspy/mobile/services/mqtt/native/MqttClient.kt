package org.rhasspy.mobile.services.mqtt.native

import org.rhasspy.mobile.services.mqtt.*

//source https://gitlab.com/napperley/kmqtt-client
/** Represents a MQTT client which can connect to the MQTT Broker. */
expect class MqttClient(
    /**
     * The MQTT Broker address which is comprised of a protocol, IPv4 address/FQDN, and port. Here is a example:
     * `tcp://192.168.1.1:1883`
     */
    brokerUrl: String,
    /** Unique MQTT client identifier. */
    clientId: String = "Default",
    persistenceType: MqttPersistence = MqttPersistence.NONE,
    /** Handles the deliveryComplete event. First argument is the delivery token. */
    onDelivered: (token: Int) -> Unit,
    /**
     * Handles the messageArrived event. First argument is the topic. Second argument is the
     * [message][MqttMessage].
     */
    onMessageReceived: (topic: String, message: MqttMessage) -> Unit,
    /** Handles the connectionLost event. First argument is the cause. */
    onDisconnect: (error: Throwable) -> Unit
) {
    /** If *true* then there is a connection to the MQTT Broker. */
    val isConnected: Boolean

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
     * Will unsubscribe from one or more topics.
     * @param topics One or more topics to unsubscribe from. Can include topic filter(s).
     * @return Will return a error if a problem has occurred.
     */
    suspend fun unsubscribe(vararg topics: String): MqttError?

    /**
     * Connects to the MQTT Broker.
     * @param connOptions The connection options to use.
     * @return Will return a [error][MqttError] if a problem has occurred.
     */
    suspend fun connect(connOptions: MqttConnectionOptions = MqttConnectionOptions()): MqttError?

    /**
     * Disconnects from the MQTT Broker.
     * @return Will return a [error][MqttError] if a problem has occurred.
     */
    suspend fun disconnect(): MqttError?
}
