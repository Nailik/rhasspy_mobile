package org.rhasspy.mobile.services.mqtt

import org.rhasspy.mobile.settings.ConfigurationSettings

data class MqttServiceParams(
    val siteId: String = ConfigurationSettings.siteId.value,
    val isMqttEnabled: Boolean = ConfigurationSettings.isMqttEnabled.value,
    val mqttHost: String = ConfigurationSettings.mqttHost.value,
    val mqttPort: Int = ConfigurationSettings.mqttPort.value,
    val retryInterval: Long = ConfigurationSettings.mqttRetryInterval.value,
    val mqttServiceConnectionOptions: MqttServiceConnectionOptions = MqttServiceConnectionOptions(
        ssl = ConfigurationSettings.isMqttSSLEnabled.value,
        connUsername = ConfigurationSettings.mqttUserName.value,
        connPassword = ConfigurationSettings.mqttPassword.value,
        connectionTimeout = ConfigurationSettings.mqttConnectionTimeout.value,
        keepAliveInterval = ConfigurationSettings.mqttKeepAliveInterval.value
    ),
    val isUseSpeechToTextMqttSilenceDetection: Boolean = ConfigurationSettings.isUseSpeechToTextMqttSilenceDetection.value
)