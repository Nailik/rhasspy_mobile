package org.rhasspy.mobile.viewmodel.configuration.mqtt

import androidx.compose.runtime.Stable
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Action.OpenMqttSSLWiki
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Action.SelectSSLCertificate
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.utils.OpenLinkUtils

@Stable
class MqttConfigurationViewModel(
    service: MqttService,
) : IConfigurationViewModel<MqttConfigurationViewState>(
    service = service,
    initialViewState = ::MqttConfigurationViewState
) {

    fun onEvent(action: MqttConfigurationUiEvent) {
        when (action) {
            is Change -> onChange(action)
            is Action -> onAction(action)
            is Consumed -> onConsumed(action)
        }
    }

    private fun onChange(change: Change) {
        updateViewState {
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
            OpenMqttSSLWiki -> openMqttSSLWikiLink()
            SelectSSLCertificate -> selectSSLCertificate()
        }
    }

    private fun onConsumed(consumed: Consumed) {
        updateViewState {
            when (consumed) {
                is ShowSnackBar -> it.copy(snackBarText = null)
            }
        }
    }

    private fun openMqttSSLWikiLink() {
        if (!OpenLinkUtils.openLink(LinkType.WikiMQTTSSL)) {
            updateViewState {
                it.copy(snackBarText = MR.strings.linkOpenFailed.stable)
            }
        }
    }

    private fun selectSSLCertificate() {
        viewModelScope.launch {
            FileUtils.selectFile(FolderType.CertificateFolder.Mqtt)?.also { path ->
                onChange(UpdateMqttKeyStoreFile(path))
            } ?: run {
                updateViewState {
                    it.copy(snackBarText = MR.strings.selectFileFailed.stable)
                }
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

}