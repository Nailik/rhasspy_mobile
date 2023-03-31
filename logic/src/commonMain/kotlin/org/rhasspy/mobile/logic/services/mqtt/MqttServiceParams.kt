package org.rhasspy.mobile.logic.services.mqtt

import org.rhasspy.mobile.data.mqtt.MqttServiceConnectionOptions
import org.rhasspy.mobile.logic.settings.ConfigurationSetting

data class MqttServiceParams(
    val siteId: String = ConfigurationSetting.siteId.value,
    val isMqttEnabled: Boolean = ConfigurationSetting.isMqttEnabled.value,
    val mqttHost: String = ConfigurationSetting.mqttHost.value,
    val mqttPort: Int = ConfigurationSetting.mqttPort.value,
    val retryInterval: Long = ConfigurationSetting.mqttRetryInterval.value,
    val mqttServiceConnectionOptions: MqttServiceConnectionOptions = MqttServiceConnectionOptions(
        isSSLEnabled = ConfigurationSetting.isMqttSSLEnabled.value,
        keyStorePath = ConfigurationSetting.mqttKeyStoreFile.value,
        connUsername = ConfigurationSetting.mqttUserName.value,
        connPassword = ConfigurationSetting.mqttPassword.value,
        connectionTimeout = ConfigurationSetting.mqttConnectionTimeout.value,
        keepAliveInterval = ConfigurationSetting.mqttKeepAliveInterval.value
    ),
    val isUseSpeechToTextMqttSilenceDetection: Boolean = ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value,
    val audioPlayingMqttSiteId: String = ConfigurationSetting.audioPlayingMqttSiteId.value
)