package org.rhasspy.mobile.services.mqtt

data class MqttServiceParams(
    val siteId: String,
    val isMqttEnabled: Boolean,
    val mqttHost: String,
    val mqttPort: Int,
    val retryInterval: Long,
    val mqttServiceConnectionOptions: MqttServiceConnectionOptions
)