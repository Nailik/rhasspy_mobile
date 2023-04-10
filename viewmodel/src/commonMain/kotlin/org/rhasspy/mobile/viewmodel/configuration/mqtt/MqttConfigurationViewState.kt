package org.rhasspy.mobile.viewmodel.configuration.mqtt

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import okio.Path
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.ServiceStateHeaderViewState

@Stable
data class MqttConfigurationViewState(
    val isMqttEnabled: Boolean,
    val mqttHost: String,
    val mqttPort: Int,
    val mqttPortText: String,
    val mqttUserName: String,
    val mqttPassword: String,
    val isMqttSSLEnabled: Boolean,
    val mqttConnectionTimeout: Int,
    val mqttConnectionTimeoutText: String,
    val mqttKeepAliveInterval: Int,
    val mqttKeepAliveIntervalText: String,
    val mqttRetryInterval: Long,
    val mqttRetryIntervalText: String,
    val mqttKeyStoreFile: Path?
): IConfigurationContentViewState() {

    companion object {
        fun getInitial() = MqttConfigurationViewState(
            isMqttEnabled = ConfigurationSetting.isMqttEnabled.value,
            mqttHost = ConfigurationSetting.mqttHost.value,
            mqttPort = ConfigurationSetting.mqttPort.value,
            mqttPortText = ConfigurationSetting.mqttPort.value.toString(),
            mqttUserName = ConfigurationSetting.mqttUserName.value,
            mqttPassword = ConfigurationSetting.mqttPassword.value,
            isMqttSSLEnabled = ConfigurationSetting.isMqttSSLEnabled.value,
            mqttConnectionTimeout = ConfigurationSetting.mqttConnectionTimeout.value,
            mqttConnectionTimeoutText = ConfigurationSetting.mqttConnectionTimeout.value.toString(),
            mqttKeepAliveInterval = ConfigurationSetting.mqttKeepAliveInterval.value,
            mqttKeepAliveIntervalText = ConfigurationSetting.mqttKeepAliveInterval.value.toString(),
            mqttRetryInterval = ConfigurationSetting.mqttRetryInterval.value,
            mqttRetryIntervalText = ConfigurationSetting.mqttRetryInterval.value.toString(),
            mqttKeyStoreFile = ConfigurationSetting.mqttKeyStoreFile.value
        )
    }

    override fun getEditViewState(serviceViewState: StateFlow<ServiceStateHeaderViewState>): IConfigurationEditViewState {
        return IConfigurationEditViewState(
            hasUnsavedChanges = !(isMqttEnabled == ConfigurationSetting.isMqttEnabled.value &&
                    mqttHost == ConfigurationSetting.mqttHost.value &&
                    mqttPort == ConfigurationSetting.mqttPort.value &&
                    mqttUserName == ConfigurationSetting.mqttUserName.value &&
                    mqttPassword == ConfigurationSetting.mqttPassword.value &&
                    isMqttSSLEnabled == ConfigurationSetting.isMqttSSLEnabled.value &&
                    mqttConnectionTimeout == ConfigurationSetting.mqttConnectionTimeout.value &&
                    mqttKeepAliveInterval == ConfigurationSetting.mqttKeepAliveInterval.value &&
                    mqttRetryInterval == ConfigurationSetting.mqttRetryInterval.value),
            isTestingEnabled = isMqttEnabled,
            serviceViewState = serviceViewState
        )
    }

    override fun save() {
        if (ConfigurationSetting.mqttKeyStoreFile.value != mqttKeyStoreFile) {
            ConfigurationSetting.mqttKeyStoreFile.value?.commonDelete()
        }

        ConfigurationSetting.isMqttEnabled.value = isMqttEnabled
        ConfigurationSetting.mqttHost.value = mqttHost
        ConfigurationSetting.mqttPort.value = mqttPort
        ConfigurationSetting.mqttUserName.value = mqttUserName
        ConfigurationSetting.mqttPassword.value = mqttPassword
        ConfigurationSetting.isMqttSSLEnabled.value = isMqttSSLEnabled
        ConfigurationSetting.mqttKeyStoreFile.value = mqttKeyStoreFile
        ConfigurationSetting.mqttConnectionTimeout.value = mqttConnectionTimeout
        ConfigurationSetting.mqttKeepAliveInterval.value = mqttKeepAliveInterval
        ConfigurationSetting.mqttRetryInterval.value = mqttRetryInterval
    }

    override fun discard() {
        if (ConfigurationSetting.mqttKeyStoreFile.value != mqttKeyStoreFile) {
            mqttKeyStoreFile?.commonDelete()
        }
    }

}