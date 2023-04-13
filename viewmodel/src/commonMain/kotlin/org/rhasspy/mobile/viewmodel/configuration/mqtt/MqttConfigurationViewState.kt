package org.rhasspy.mobile.viewmodel.configuration.mqtt

import androidx.compose.runtime.Stable
import okio.Path
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.platformspecific.toLongOrZero
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState

@Stable
data class MqttConfigurationViewState(
    val isMqttEnabled: Boolean = ConfigurationSetting.isMqttEnabled.value,
    val mqttHost: String = ConfigurationSetting.mqttHost.value,
    val mqttPortText: String = ConfigurationSetting.mqttPort.value.toString(),
    val mqttUserName: String = ConfigurationSetting.mqttUserName.value,
    val mqttPassword: String = ConfigurationSetting.mqttPassword.value,
    val isMqttSSLEnabled: Boolean = ConfigurationSetting.isMqttSSLEnabled.value,
    val mqttConnectionTimeoutText: String = ConfigurationSetting.mqttConnectionTimeout.value.toString(),
    val mqttKeepAliveIntervalText: String = ConfigurationSetting.mqttKeepAliveInterval.value.toString(),
    val mqttRetryIntervalText: String = ConfigurationSetting.mqttKeepAliveInterval.value.toString(),
    val mqttKeyStoreFile: Path? = ConfigurationSetting.mqttKeyStoreFile.value
): IConfigurationEditViewState {

    override val hasUnsavedChanges: Boolean
        get() = !(isMqttEnabled == ConfigurationSetting.isMqttEnabled.value &&
                mqttHost == ConfigurationSetting.mqttHost.value &&
                mqttPort == ConfigurationSetting.mqttPort.value &&
                mqttUserName == ConfigurationSetting.mqttUserName.value &&
                mqttPassword == ConfigurationSetting.mqttPassword.value &&
                isMqttSSLEnabled == ConfigurationSetting.isMqttSSLEnabled.value &&
                mqttConnectionTimeout == ConfigurationSetting.mqttConnectionTimeout.value &&
                mqttKeepAliveInterval == ConfigurationSetting.mqttKeepAliveInterval.value &&
                mqttRetryInterval == ConfigurationSetting.mqttRetryInterval.value &&
                mqttKeyStoreFile == ConfigurationSetting.mqttKeyStoreFile.value)

    override val isTestingEnabled: Boolean get() = isMqttEnabled

    val mqttPort: Int get() = mqttPortText.toIntOrZero()
    val mqttConnectionTimeout: Int get() = mqttConnectionTimeoutText.toIntOrZero()
    val mqttKeepAliveInterval: Int get() = mqttKeepAliveIntervalText.toIntOrZero()
    val mqttRetryInterval: Long get() = mqttRetryIntervalText.toLongOrZero()

}