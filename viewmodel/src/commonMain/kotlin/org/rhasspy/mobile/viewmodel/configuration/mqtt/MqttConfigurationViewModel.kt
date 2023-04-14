package org.rhasspy.mobile.viewmodel.configuration.mqtt

import androidx.compose.runtime.Stable
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.data.mqtt.MqttServiceConnectionOptions
import org.rhasspy.mobile.logic.openLink
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.mqtt.MqttServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.update
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiAction.Change
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiAction.Change.SetMqttEnabled
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiAction.Change.SetMqttSSLEnabled
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiAction.Change.UpdateMqttConnectionTimeout
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiAction.Change.UpdateMqttHost
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiAction.Change.UpdateMqttKeepAliveInterval
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiAction.Change.UpdateMqttKeyStoreFile
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiAction.Change.UpdateMqttPassword
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiAction.Change.UpdateMqttPort
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiAction.Change.UpdateMqttRetryInterval
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiAction.Change.UpdateMqttUserName
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiAction.Navigate

@Stable
class MqttConfigurationViewModel(
    service: MqttService,
    testRunner: MqttConfigurationTest
) : IConfigurationViewModel<MqttConfigurationTest, MqttConfigurationViewState>(
    service = service,
    testRunner = testRunner,
    initialViewState = ::MqttConfigurationViewState
) {

    fun onAction(action: MqttConfigurationUiAction) {
        when (action) {
            is Change -> onChange(action)
            is Navigate -> onNavigate(action)
        }
    }

    private fun onChange(change: Change) {
        contentViewState.update {
            when (change) {
                is SetMqttEnabled -> it.copy(isMqttEnabled = change.enabled)
                is SetMqttSSLEnabled -> it.copy(isMqttSSLEnabled = change.enabled)
                is UpdateMqttConnectionTimeout -> it.copy(mqttConnectionTimeoutText = change.value)
                is UpdateMqttHost -> it.copy(mqttHost = change.value)
                is UpdateMqttKeepAliveInterval -> it.copy(mqttKeepAliveIntervalText = change.value)
                is UpdateMqttPassword -> it.copy(mqttPassword = change.value)
                is UpdateMqttPort -> it.copy(mqttPortText = change.value)
                is UpdateMqttRetryInterval -> it.copy(mqttRetryIntervalText = change.value)
                is UpdateMqttUserName -> it.copy(mqttUserName = change.value)
                is UpdateMqttKeyStoreFile -> it.copy(mqttKeyStoreFile = change.value)
            }
        }
    }

    private fun onNavigate(navigate: Navigate) {
        when (navigate) {
            Navigate.OpenMqttSSLWiki -> openLink("https://github.com/Nailik/rhasspy_mobile/wiki/MQTT#enable-ssl")
            Navigate.SelectSSLCertificate -> selectSSLCertificate()
        }
    }

    private fun selectSSLCertificate() {
        viewModelScope.launch {
            FileUtils.selectFile(FolderType.CertificateFolder.Mqtt)?.also { path ->
                onChange(UpdateMqttKeyStoreFile(path))
            }
        }
    }

    override fun onSave() {
        if (ConfigurationSetting.mqttKeyStoreFile.value != data.mqttKeyStoreFile) {
            ConfigurationSetting.mqttKeyStoreFile.value?.commonDelete()
        }

        ConfigurationSetting.isMqttEnabled.value = data.isMqttEnabled
        ConfigurationSetting.mqttHost.value = data.mqttHost
        ConfigurationSetting.mqttPort.value = data.mqttPort
        ConfigurationSetting.mqttUserName.value = data.mqttUserName
        ConfigurationSetting.mqttPassword.value = data.mqttPassword
        ConfigurationSetting.isMqttSSLEnabled.value = data.isMqttSSLEnabled
        ConfigurationSetting.mqttKeyStoreFile.value = data.mqttKeyStoreFile
        ConfigurationSetting.mqttConnectionTimeout.value = data.mqttConnectionTimeout
        ConfigurationSetting.mqttKeepAliveInterval.value = data.mqttKeepAliveInterval
        ConfigurationSetting.mqttRetryInterval.value = data.mqttRetryInterval
    }

    override fun onDiscard() {
        if (ConfigurationSetting.mqttKeyStoreFile.value != data.mqttKeyStoreFile) {
            data.mqttKeyStoreFile?.commonDelete()
        }
    }

    /**
     * test unsaved data configuration
     */
    override fun initializeTestParams() {
        //initialize test params
        get<MqttServiceParams> {
            parametersOf(
                MqttServiceParams(
                    isMqttEnabled = data.isMqttEnabled,
                    mqttHost = data.mqttHost,
                    mqttPort = data.mqttPort,
                    retryInterval = data.mqttRetryInterval,
                    mqttServiceConnectionOptions = MqttServiceConnectionOptions(
                        isSSLEnabled = data.isMqttSSLEnabled,
                        keyStorePath = data.mqttKeyStoreFile,
                        connUsername = data.mqttUserName,
                        connPassword = data.mqttPassword,
                        connectionTimeout = data.mqttConnectionTimeout,
                        keepAliveInterval = data.mqttKeepAliveInterval
                    )
                )
            )
        }
    }

}