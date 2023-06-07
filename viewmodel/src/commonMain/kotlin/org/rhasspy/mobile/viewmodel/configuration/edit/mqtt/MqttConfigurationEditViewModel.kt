package org.rhasspy.mobile.viewmodel.configuration.edit.mqtt

import androidx.compose.runtime.Stable
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.utils.OpenLinkUtils
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.mqtt.MqttConfigurationUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.MqttConfigurationScreenDestination.EditScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.MqttConfigurationScreenDestination.TestScreen

@Stable
class MqttConfigurationEditViewModel(
    service: MqttService
) : IConfigurationEditViewModel<MqttConfigurationViewState>(
    service = service,
    initialViewState = ::MqttConfigurationViewState,
    testPageDestination = TestScreen
) {

    val screen = navigator.topScreen(EditScreen)

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
            BackClick -> navigator.onBackPressed()
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
        if (!get<OpenLinkUtils>().openLink(LinkType.WikiMQTTSSL)) {
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