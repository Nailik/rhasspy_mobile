package org.rhasspy.mobile.viewmodel.configuration.connections.mqtt

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import okio.Path.Companion.toPath
import org.rhasspy.mobile.data.data.toIntOrNullOrConstant
import org.rhasspy.mobile.data.data.toLongOrNullOrConstant
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Change.*

@Stable
class MqttConnectionConfigurationViewModel(
    private val mapper: MqttConnectionConfigurationDataMapper,
    service: IMqttConnection
) : ConfigurationViewModel(
    service = null
) {

    private val initialData get() = mapper(ConfigurationSetting.mqttConnection.value)
    private val _viewState = MutableStateFlow(MqttConnectionConfigurationViewState(initialData))
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::initialData,
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
                    is SetMqttEnabled              -> copy(isEnabled = change.enabled)
                    is SetMqttSSLEnabled           -> copy(isSSLEnabled = change.enabled)
                    is UpdateMqttConnectionTimeout -> copy(connectionTimeout = change.timeout.toIntOrNullOrConstant())
                    is UpdateMqttHost              -> copy(host = change.host)
                    is UpdateMqttKeepAliveInterval -> copy(keepAliveInterval = change.keepAliveInterval.toIntOrNullOrConstant())
                    is UpdateMqttPassword          -> copy(password = change.password)
                    is UpdateMqttRetryInterval     -> copy(retryInterval = change.retryInterval.toLongOrNullOrConstant())
                    is UpdateMqttUserName          -> copy(userName = change.userName)
                    is UpdateMqttKeyStoreFile      -> copy(keystoreFile = change.file.name)
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
        if (ConfigurationSetting.mqttConnection.value.keystoreFile != _viewState.value.editData.keystoreFile) {
            _viewState.value.editData.keystoreFile?.toPath()?.commonDelete()
        }
        _viewState.update { it.copy(editData = initialData) }
    }

    override fun onSave() {
        if (ConfigurationSetting.mqttConnection.value.keystoreFile != _viewState.value.editData.keystoreFile) {
            ConfigurationSetting.mqttConnection.value.keystoreFile?.toPath()?.commonDelete()
        }
        ConfigurationSetting.mqttConnection.value = mapper(_viewState.value.editData)
        _viewState.update { it.copy(editData = initialData) }
    }

}