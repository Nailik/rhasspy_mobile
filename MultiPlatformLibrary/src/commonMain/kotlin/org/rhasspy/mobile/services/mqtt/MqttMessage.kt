package org.rhasspy.mobile.services.mqtt

/** Represents a MQTT message. Doesn't include the topic name. */
data class MqttMessage(
    /** The data in the message. */
    val payload: ByteArray,
    /** Unique identifier for the message. */
    var msgId: Int = 0,
    /** Quality of service. */
    val qos: MqttQos = MqttQos.AT_MOST_ONCE,
    /** If *true* then the message is kept by the MQTT Broker. */
    val retained: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MqttMessage

        if (!payload.contentEquals(other.payload)) return false
        if (msgId != other.msgId) return false
        if (qos != other.qos) return false
        if (retained != other.retained) return false

        return true
    }

    override fun hashCode(): Int {
        var result = payload.contentHashCode()
        result = 31 * result + msgId
        result = 31 * result + qos.hashCode()
        result = 31 * result + retained.hashCode()
        return result
    }
}
