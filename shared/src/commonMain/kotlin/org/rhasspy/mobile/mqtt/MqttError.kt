package org.rhasspy.mobile.mqtt

/**
 * Models a MQTT error.
 * @param msg The message.
 * @param statusCode The MQTT status code.
 */
data class MqttError(val msg: String, val statusCode: MqttStatus)
