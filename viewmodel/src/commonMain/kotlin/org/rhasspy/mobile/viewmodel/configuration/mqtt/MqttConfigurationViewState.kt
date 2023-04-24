package org.rhasspy.mobile.viewmodel.configuration.mqtt

import androidx.compose.runtime.Stable
import okio.Path
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.platformspecific.toLongOrZero
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState

@Stable
data class MqttConfigurationViewState internal constructor(
    val isMqttEnabled: Boolean = ConfigurationSetting.isMqttEnabled.value,
    val mqttHost: String = ConfigurationSetting.mqttHost.value,
    val mqttPortText: String = ConfigurationSetting.mqttPort.value.toString(),
    val mqttUserName: String = ConfigurationSetting.mqttUserName.value,
    val mqttPassword: String = ConfigurationSetting.mqttPassword.value,
    val isMqttSSLEnabled: Boolean = ConfigurationSetting.isMqttSSLEnabled.value,
    val mqttConnectionTimeoutText: String = ConfigurationSetting.mqttConnectionTimeout.value.toString(),
    val mqttKeepAliveIntervalText: String = ConfigurationSetting.mqttKeepAliveInterval.value.toString(),
    val mqttRetryIntervalText: String = ConfigurationSetting.mqttRetryInterval.value.toString(),
    val mqttKeyStoreFile: Path? = ConfigurationSetting.mqttKeyStoreFile.value
) : IConfigurationEditViewState() {

    val mqttPort: Int get() = mqttPortText.toIntOrZero()
    val mqttConnectionTimeout: Int get() = mqttConnectionTimeoutText.toIntOrZero()
    val mqttKeepAliveInterval: Int get() = mqttKeepAliveIntervalText.toIntOrZero()
    val mqttRetryInterval: Long get() = mqttRetryIntervalText.toLongOrZero()
    val mqttKeyStoreFileName: String? get() = mqttKeyStoreFile?.name

    override val isTestingEnabled: Boolean get() = isMqttEnabled


}