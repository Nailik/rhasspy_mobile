package org.rhasspy.mobile.viewmodel.configuration.mqtt

import androidx.compose.runtime.Stable
import okio.Path
import org.rhasspy.mobile.platformspecific.toStringOrEmpty
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class MqttConfigurationViewState internal constructor(
    override val editData: MqttConfigurationData
) : IConfigurationViewState {

    @Stable
    data class MqttConfigurationData internal constructor(
        val isMqttEnabled: Boolean = ConfigurationSetting.isMqttEnabled.value,
        val mqttHost: String = ConfigurationSetting.mqttHost.value,
        val mqttPort: Int? = ConfigurationSetting.mqttPort.value,
        val mqttUserName: String = ConfigurationSetting.mqttUserName.value,
        val mqttPassword: String = ConfigurationSetting.mqttPassword.value,
        val isMqttSSLEnabled: Boolean = ConfigurationSetting.isMqttSSLEnabled.value,
        val mqttConnectionTimeout: Long? = ConfigurationSetting.mqttConnectionTimeout.value,
        val mqttKeepAliveInterval: Long? = ConfigurationSetting.mqttKeepAliveInterval.value,
        val mqttRetryInterval: Long? = ConfigurationSetting.mqttRetryInterval.value,
        val mqttKeyStoreFile: Path? = ConfigurationSetting.mqttKeyStoreFile.value
    ) : IConfigurationData {

        val mqttPortText: String = mqttPort.toStringOrEmpty()
        val mqttConnectionTimeoutText: String = mqttConnectionTimeout.toStringOrEmpty()
        val mqttKeepAliveIntervalText: String = mqttKeepAliveInterval.toStringOrEmpty()
        val mqttRetryIntervalText: String = mqttRetryInterval.toStringOrEmpty()
        val mqttKeyStoreFileName: String? = mqttKeyStoreFile?.name

    }

}