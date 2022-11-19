package org.rhasspy.mobile.services.mqtt

import org.rhasspy.mobile.settings.ConfigurationSettings

data class MqttServiceParams(
    private val siteId: String,
    private val isMqttEnabled: Boolean,
    private val mqttHost: String,
    private val mqttPort: Int,
    private val mqttUserName: String,
    private val mqttPassword: String,
    private val isMqttSSLEnabled: Boolean,
    private val mqttConnectionTimeout: Int,
    private val mqttKeepAliveInterval: Int,
    private val mqttRetryInterval: Int,
) {
    companion object {

        fun loadFromConfiguration(): MqttServiceParams {
            return MqttServiceParams(
                siteId = ConfigurationSettings.siteId.value,
                isMqttEnabled = ConfigurationSettings.isMqttEnabled.value,
                mqttHost = ConfigurationSettings.mqttHost.value,
                mqttPort = ConfigurationSettings.mqttPort.value,
                mqttUserName = ConfigurationSettings.mqttUserName.value,
                mqttPassword = ConfigurationSettings.mqttPassword.value,
                isMqttSSLEnabled = ConfigurationSettings.isMqttSSLEnabled.value,
                mqttConnectionTimeout = ConfigurationSettings.mqttConnectionTimeout.value,
                mqttKeepAliveInterval = ConfigurationSettings.mqttKeepAliveInterval.value,
                mqttRetryInterval = ConfigurationSettings.mqttRetryInterval.value
            )
        }

    }
}