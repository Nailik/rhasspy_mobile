package org.rhasspy.mobile.services.mqtt

/** Type of persistence to use for MQTT. */
enum class MqttPersistence(val value: Int) {
    NONE(0),
    FILE(1),
    MEMORY(2);
}
