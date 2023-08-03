package org.rhasspy.mobile.logic.services.mqtt

import org.rhasspy.mobile.data.mqtt.MqttServiceConnectionOptions

internal data class MqttServiceParams(
    val siteId: String,
    val isMqttEnabled: Boolean,
    val mqttHost: String,
    val mqttPort: Int,
    val retryInterval: Long,
    val mqttServiceConnectionOptions: MqttServiceConnectionOptions,
    val isUseSpeechToTextMqttSilenceDetection: Boolean,
    val audioPlayingMqttSiteId: String
)