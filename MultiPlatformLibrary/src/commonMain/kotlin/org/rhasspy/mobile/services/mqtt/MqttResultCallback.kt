package org.rhasspy.mobile.services.mqtt

import com.benasher44.uuid.Uuid

data class MqttResultCallback(val uuid: Uuid, val resultTopics: Array<String>, val callback: suspend (mqttMessage: MqttMessage) -> Unit) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MqttResultCallback

        if (uuid != other.uuid) return false
        if (!resultTopics.contentEquals(other.resultTopics)) return false
        if (callback != other.callback) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + resultTopics.contentHashCode()
        result = 31 * result + callback.hashCode()
        return result
    }
}