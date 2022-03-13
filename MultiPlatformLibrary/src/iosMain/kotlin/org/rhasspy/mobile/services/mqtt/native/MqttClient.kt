package org.rhasspy.mobile.services.mqtt.native

import org.rhasspy.mobile.services.mqtt.MqttConnectionOptions
import org.rhasspy.mobile.services.mqtt.MqttError
import org.rhasspy.mobile.services.mqtt.MqttMessage
import org.rhasspy.mobile.services.mqtt.MqttQos

actual class MqttClient {
    /**
     * The MQTT Broker address which is comprised of a protocol, IPv4 address/FQDN, and port. Here is a example:
     * `tcp://192.168.1.1:1883`
     */
    actual val brokerUrl: String
        get() = TODO("Not yet implemented")

    /** Unique MQTT client identifier. */
    actual val clientId: String
        get() = TODO("Not yet implemented")

    /** Handles the deliveryComplete event. First argument is the delivery token. */
    @Suppress("UNUSED_PARAMETER")
    actual var deliveryCompleteHandler: (Int) -> Unit
        get() = TODO("Not yet implemented")
        set(value) {}

    /** Handles the connectionLost event. First argument is the cause. */
    /** Handles the deliveryComplete event. First argument is the delivery token. */
    @Suppress("UNUSED_PARAMETER")
    actual var connectionLostHandler: (String) -> Unit
        get() = TODO("Not yet implemented")
        set(value) {}

    /**
     * Handles the messageArrived event. First argument is the topic. Second argument is the
     * [message][MqttMessage].
     */
    /** Handles the deliveryComplete event. First argument is the delivery token. */
    @Suppress("UNUSED_PARAMETER")
    actual var messageArrivedHandler: (String, MqttMessage) -> Unit
        get() = TODO("Not yet implemented")
        set(value) {}

    /** If *true* then there is a connection to the MQTT Broker. */
    actual val isConnected: Boolean
        get() = TODO("Not yet implemented")

    /**
     * Publishes a message to the MQTT Broker.
     * @param topic The MQTT topic to use.
     * @param msg The MQTT message which includes the payload.
     * @param timeout Timeout for publishing in milliseconds.
     * @return Will return a [error][MqttError] if a problem has occurred.
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