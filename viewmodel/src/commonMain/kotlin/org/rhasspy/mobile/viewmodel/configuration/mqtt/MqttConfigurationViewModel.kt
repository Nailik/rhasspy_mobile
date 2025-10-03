package org.rhasspy.mobile.viewmodel.configuration.mqtt

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.logic.services.mqtt.IMqttService
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.toIntOrNullOrConstant
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.platformspecific.toLongOrNullOrConstant
import org.rhasspy.mobile.platformspecific.toLongOrZero
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Action.OpenMqttSSLWiki
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Action.SelectSSLCertificate
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Action.RemoveSSLCertificate
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.SetMqttEnabled
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.SetMqttSSLEnabled
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttConnectionTimeout
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttHost
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttKeepAliveInterval
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttKeyStoreFile
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttKeyStorePassword
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttPassword
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttPort
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttRetryInterval
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.UpdateMqttUserName
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationViewState.MqttConfigurationData

@Stable
class MqttConfigurationViewModel(
    service: IMqttService,
) : ConfigurationViewModel(
    service = service
) {

    private val _viewState = MutableStateFlow(MqttConfigurationViewState(MqttConfigurationData()))
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>,
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::MqttConfigurationData,
            viewState = viewState,
            configurationViewState = configurationViewState
        )
    }

    fun onEvent(action: MqttConfigurationUiEvent) {
        when (action) {
            is Change -> onChange(action)
            is Action -> onAction(action)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SetMqttEnabled -> copy(isMqttEnabled = change.enabled)
                    is SetMqttSSLEnabled -> copy(isMqttSSLEnabled = change.enabled)
                    is UpdateMqttConnectionTimeout -> copy(mqttConnectionTimeout = change.timeout.toLongOrNullOrConstant())
                    is UpdateMqttHost -> copy(mqttHost = change.host)
                    is UpdateMqttKeepAliveInterval -> copy(mqttKeepAliveInterval = change.keepAliveInterval.toLongOrNullOrConstant())
                    is UpdateMqttPassword -> copy(mqttPassword = change.password)
                    is UpdateMqttPort -> copy(mqttPort = change.port.toIntOrNullOrConstant())
                    is UpdateMqttRetryInterval -> copy(mqttRetryInterval = change.retryInterval.toLongOrNullOrConstant())
                    is UpdateMqttUserName -> copy(mqttUserName = change.userName)
                    is UpdateMqttKeyStoreFile -> copy(
                        mqttKeyStoreFile = change.file,
                        isKeyStoreFileTextVisible = true
                    )
                    is UpdateMqttKeyStorePassword -> copy(mqttKeyStorePassword = change.mqttKeyStorePassword)
                }
            })
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            OpenMqttSSLWiki -> openLink(LinkType.WikiMQTTSSL)
            SelectSSLCertificate -> selectFile(FolderType.CertificateFolder.Mqtt) { path ->
                onChange(
                    UpdateMqttKeyStoreFile(path)
                )
            }

            RemoveSSLCertificate -> {
                val current = _viewState.value.editData.mqttKeyStoreFile
                val configured = ConfigurationSetting.mqttKeyStoreFile.value
                if (current != null && current != configured) {
                    current.commonDelete()
                }
                _viewState.update { vs ->
                    vs.copy(editData = vs.editData.copy(
                        mqttKeyStoreFile = null,
                        isKeyStoreFileTextVisible = false
                    ))
                }
            }

            BackClick -> navigator.onBackPressed()
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
            ConfigurationSetting.mqttKeyStorePassword.value = mqttKeyStorePassword
            ConfigurationSetting.mqttConnectionTimeout.value = mqttConnectionTimeout.toLongOrZero()
            ConfigurationSetting.mqttKeepAliveInterval.value = mqttKeepAliveInterval.toLongOrZero()
            ConfigurationSetting.mqttRetryInterval.value = mqttRetryInterval.toLongOrZero()
        }
    }

}