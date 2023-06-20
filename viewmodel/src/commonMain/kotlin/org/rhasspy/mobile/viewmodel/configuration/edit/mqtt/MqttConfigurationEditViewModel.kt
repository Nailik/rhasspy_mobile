package org.rhasspy.mobile.viewmodel.configuration.edit.mqtt

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.platformspecific.toLongOrZero
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationViewState.MqttConfigurationData

@Stable
class MqttConfigurationEditViewModel(
    service: MqttService,
    private val viewStateCreator: ConfigurationEditViewStateCreator
) : IConfigurationEditViewModel(
    service = service
) {

    private val initialConfigurationData = MqttConfigurationData()

    private val _editData = MutableStateFlow(initialConfigurationData)
    private val _viewState = MutableStateFlow(MqttConfigurationViewState(initialConfigurationData))
    val viewState = combineState(_viewState, _editData) { viewState, editData ->
        viewState.copy(editData = editData)
    }

    override fun initViewStateCreator(
        configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>
    ): StateFlow<ConfigurationEditViewState> {
        return viewStateCreator(
            init = ::MqttConfigurationData,
            editData = _editData,
            configurationEditViewState = configurationEditViewState
        )
    }

    fun onEvent(action: MqttConfigurationUiEvent) {
        when (action) {
            is Change -> onChange(action)
            is Action -> onAction(action)
        }
    }

    private fun onChange(change: Change) {
        _editData.update {
            when (change) {
                is SetMqttEnabled -> it.copy(isMqttEnabled = change.enabled)
                is SetMqttSSLEnabled -> it.copy(isMqttSSLEnabled = change.enabled)
                is UpdateMqttConnectionTimeout -> it.copy(mqttConnectionTimeout = change.timeout.toLongOrNull())
                is UpdateMqttHost -> it.copy(mqttHost = change.host)
                is UpdateMqttKeepAliveInterval -> it.copy(mqttKeepAliveInterval = change.keepAliveInterval.toLongOrNull())
                is UpdateMqttPassword -> it.copy(mqttPassword = change.password)
                is UpdateMqttPort -> it.copy(mqttPort = change.port.toIntOrNull())
                is UpdateMqttRetryInterval -> it.copy(mqttRetryInterval = change.retryInterval.toLongOrNull())
                is UpdateMqttUserName -> it.copy(mqttUserName = change.userName)
                is UpdateMqttKeyStoreFile -> it.copy(mqttKeyStoreFile = change.file)
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            OpenMqttSSLWiki -> openLink(LinkType.WikiMQTTSSL)
            SelectSSLCertificate -> selectFile(FolderType.CertificateFolder.Mqtt) { path ->
                onChange(UpdateMqttKeyStoreFile(path))
            }

            BackClick -> navigator.onBackPressed()
        }
    }

    override fun onSave() {
        with(_editData.value) {
            if (ConfigurationSetting.mqttKeyStoreFile.value != mqttKeyStoreFile) {
                ConfigurationSetting.mqttKeyStoreFile.value?.commonDelete()
            }

            ConfigurationSetting.isMqttEnabled.value = isMqttEnabled
            ConfigurationSetting.mqttHost.value = mqttHost
            ConfigurationSetting.mqttPort.value = mqttPort.toIntOrZero()
            ConfigurationSetting.mqttUserName.value = mqttUserName
            ConfigurationSetting.mqttPassword.value = mqttPassword
            ConfigurationSetting.isMqttSSLEnabled.value = isMqttSSLEnabled
            ConfigurationSetting.mqttKeyStoreFile.value = mqttKeyStoreFile
            ConfigurationSetting.mqttConnectionTimeout.value = mqttConnectionTimeout.toLongOrZero()
            ConfigurationSetting.mqttKeepAliveInterval.value = mqttKeepAliveInterval.toLongOrZero()
            ConfigurationSetting.mqttRetryInterval.value = mqttRetryInterval.toLongOrZero()
        }
    }

    override fun onDiscard() {
        with(_editData.value) {
            if (ConfigurationSetting.mqttKeyStoreFile.value != mqttKeyStoreFile) {
                mqttKeyStoreFile?.commonDelete()
            }
        }
    }

}