package org.rhasspy.mobile.viewmodel.configuration.connections.mqtt

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.logic.connections.mqtt.IMqttService
import org.rhasspy.mobile.platformspecific.*
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationViewState.MqttConfigurationData

@Stable
class MqttConnectionConfigurationViewModel(
    service: IMqttService
) : ConfigurationViewModel(
    service = service
) {

    private val _viewState = MutableStateFlow(MqttConnectionConfigurationViewState(MqttConfigurationData()))
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::MqttConfigurationData,
            viewState = viewState,
            configurationViewState = configurationViewState
        )
    }

    fun onEvent(action: MqttConnectionConfigurationUiEvent) {
        when (action) {
            is Change -> onChange(action)
            is Action -> onAction(action)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SetMqttEnabled              -> copy(isMqttEnabled = change.enabled)
                    is SetMqttSSLEnabled           -> copy(isMqttSSLEnabled = change.enabled)
                    is UpdateMqttConnectionTimeout -> copy(mqttConnectionTimeout = change.timeout.toLongOrNullOrConstant())
                    is UpdateMqttHost              -> copy(mqttHost = change.host)
                    is UpdateMqttKeepAliveInterval -> copy(mqttKeepAliveInterval = change.keepAliveInterval.toLongOrNullOrConstant())
                    is UpdateMqttPassword          -> copy(mqttPassword = change.password)
                    is UpdateMqttPort              -> copy(mqttPort = change.port.toIntOrNullOrConstant())
                    is UpdateMqttRetryInterval     -> copy(mqttRetryInterval = change.retryInterval.toLongOrNullOrConstant())
                    is UpdateMqttUserName          -> copy(mqttUserName = change.userName)
                    is UpdateMqttKeyStoreFile      -> copy(mqttKeyStoreFile = change.file)
                }
            })
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            OpenMqttSSLWiki      -> openLink(LinkType.WikiMQTTSSL)
            SelectSSLCertificate -> selectFile(FolderType.CertificateFolder.Mqtt) { path -> onChange(UpdateMqttKeyStoreFile(path)) }
            BackClick            -> navigator.onBackPressed()
        }
    }

    override fun onDiscard() {
        with(_viewState.value.editData) {
            if (ConfigurationSetting.mqttKeyStoreFile.value != mqttKeyStoreFile) {
                mqttKeyStoreFile?.commonDelete()
            }
        }
        _viewState.update { it.copy(editData = MqttConfigurationData()) }
    }

    override fun onSave() {
        with(_viewState.value.editData) {
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

}