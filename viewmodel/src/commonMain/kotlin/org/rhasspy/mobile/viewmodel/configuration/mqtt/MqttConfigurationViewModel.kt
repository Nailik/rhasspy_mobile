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
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Action.OpenMqttSSLWiki
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Action.SelectSSLCertificate
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.*

@Stable
class MqttConfigurationViewModel(
    service: MqttService,
) : IConfigurationViewModel<MqttConfigurationViewState>(
    service = service,
    initialViewState = ::MqttConfigurationViewState
) {

    fun onAction(action: MqttConfigurationUiEvent) {
        when (action) {
            is Change -> onChange(action)
            is Action -> onAction(action)
        }
    }

    private fun onChange(change: Change) {
        contentViewState.update {
            when (change) {
                is SetMqttEnabled -> it.copy(isMqttEnabled = change.enabled)
                is SetMqttSSLEnabled -> it.copy(isMqttSSLEnabled = change.enabled)
                is UpdateMqttConnectionTimeout -> it.copy(mqttConnectionTimeoutText = change.timeout)
                is UpdateMqttHost -> it.copy(mqttHost = change.host)
                is UpdateMqttKeepAliveInterval -> it.copy(mqttKeepAliveIntervalText = change.keepAliveInterval)
                is UpdateMqttPassword -> it.copy(mqttPassword = change.password)
                is UpdateMqttPort -> it.copy(mqttPortText = change.port)
                is UpdateMqttRetryInterval -> it.copy(mqttRetryIntervalText = change.retryInterval)
                is UpdateMqttUserName -> it.copy(mqttUserName = change.userName)
                is UpdateMqttKeyStoreFile -> it.copy(mqttKeyStoreFile = change.file)
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            OpenMqttSSLWiki -> openLink("https://github.com/Nailik/rhasspy_mobile/wiki/MQTT#enable-ssl")
            SelectSSLCertificate -> selectSSLCertificate()
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
        get<MqttService>()
    }

}