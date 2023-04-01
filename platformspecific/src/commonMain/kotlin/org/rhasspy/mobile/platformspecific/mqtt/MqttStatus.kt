package org.rhasspy.mobile.platformspecific.mqtt

/** Contains all possible MQTT states. */
enum class MqttStatus {
    SUCCESS,
    UNACCEPTABLE_PROTOCOL,
    IDENTIFIER_REJECTED,
    SERVER_UNAVAILABLE,
    INVALID_CREDENTIALS,
    NOT_AUTHORIZED,
    ALREADY_CONNECTED,
    MSG_DELIVERY_FAILED,
    MSG_PERSISTENCE_FAILED,
    SUBSCRIBE_FAILED,
    UNKNOWN
}
