package org.rhasspy.mobile.viewmodel.configuration.edit.mqtt

import androidx.compose.runtime.Stable
import okio.Path
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.platformspecific.toLongOrZero
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState

@Stable
data class MqttConfigurationViewState internal constructor(
    val editData: MqttConfigurationData
) {

    @Stable
    data class MqttConfigurationData internal constructor(
        val isMqttEnabled: Boolean = ConfigurationSetting.isMqttEnabled.value,
        val mqttHost: String = ConfigurationSetting.mqttHost.value,
        val mqttPort: Int? = ConfigurationSetting.mqttPort.value,
        val mqttUserName: String = ConfigurationSetting.mqttUserName.value,
        val mqttPassword: String = ConfigurationSetting.mqttPassword.value,
        val isMqttSSLEnabled: Boolean = ConfigurationSetting.isMqttSSLEnabled.value,
        val mqttConnectionTimeout: Int? = ConfigurationSetting.mqttConnectionTimeout.value,
        val mqttKeepAliveInterval: Int? = ConfigurationSetting.mqttKeepAliveInterval.value,
        val mqttRetryInterval: Long? = ConfigurationSetting.mqttRetryInterval.value,
        val mqttKeyStoreFile: Path? = ConfigurationSetting.mqttKeyStoreFile.value
    ) {

        val mqttPortText: String = mqttPort.toString()
        val mqttConnectionTimeoutText: String = mqttConnectionTimeout.toString()
        val mqttKeepAliveIntervalText: String = mqttKeepAliveInterval.toString()
        val mqttRetryIntervalText: String = mqttRetryInterval.toString()
        val mqttKeyStoreFileName: String? = mqttKeyStoreFile?.name

    }

}

//override val isTestingEnabled: Boolean get() = isMqttEnabled