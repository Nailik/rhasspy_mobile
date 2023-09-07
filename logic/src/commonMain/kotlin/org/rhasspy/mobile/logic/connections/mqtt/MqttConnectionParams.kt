package org.rhasspy.mobile.logic.connections.mqtt

import org.rhasspy.mobile.data.connection.MqttConnectionData

internal data class MqttConnectionParams(
    val siteId: String,
    val mqttConnectionData: MqttConnectionData,
    val isUseSpeechToTextMqttSilenceDetection: Boolean,
    val audioPlayingMqttSiteId: String
)