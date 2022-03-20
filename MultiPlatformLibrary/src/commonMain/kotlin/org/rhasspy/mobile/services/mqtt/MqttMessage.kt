package org.rhasspy.mobile.services.mqtt

/** Represents a MQTT message. Doesn't include the topic name. */
data class MqttMessage(
    /** The data in the message. */
    val payload: Any,
    /** Unique identifier for the message. */
    var msgId: Int = 0,
    /** Quality of service. */
    val qos: MqttQos = MqttQos.AT_MOST_ONCE,
    /** If *true* then the message is kept by the MQTT Broker. */
    val retained: Boolean = false
)

