package org.rhasspy.mobile.viewmodel.configuration.connections.mqtt

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import okio.Path.Companion.toPath
import org.rhasspy.mobile.data.data.takeInt
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Action.OpenMqttSSLWiki
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Action.SelectSSLCertificate
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class MqttConnectionConfigurationViewModel(
    private val mapper: MqttConnectionConfigurationDataMapper,
    mqttConnection: IMqttConnection,
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(
        MqttConnectionConfigurationViewState(
            editData = mapper(ConfigurationSetting.mqttConnection.value),
            connectionState = mqttConnection.connectionState,
        )
    )
    val viewState = _viewState.readOnly

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
                    is UpdateMqttConnectionTimeout -> copy(connectionTimeout = change.timeout.takeInt())
                    is UpdateMqttHost              -> copy(host = change.host)
                    is UpdateMqttKeepAliveInterval -> copy(keepAliveInterval = change.keepAliveInterval.takeInt())
                    is UpdateMqttPassword          -> copy(password = change.password)
                    is UpdateMqttRetryInterval     -> copy(retryInterval = change.retryInterval.takeInt())
                    is UpdateMqttUserName          -> copy(userName = change.userName)
                    is UpdateMqttKeyStoreFile      -> copy(keystoreFile = change.file.name)
                }
            })
        }
        if (ConfigurationSetting.mqttConnection.value.keystoreFile != _viewState.value.editData.keystoreFile) {
            ConfigurationSetting.mqttConnection.value.keystoreFile?.toPath()?.commonDelete()
        }
        ConfigurationSetting.mqttConnection.value = mapper(_viewState.value.editData)
    }

    private fun onAction(action: Action) {
        when (action) {
            OpenMqttSSLWiki      -> openLink(LinkType.WikiMQTTSSL)
            SelectSSLCertificate -> selectFile(FolderType.CertificateFolder.Mqtt) { path -> onChange(UpdateMqttKeyStoreFile(path)) }
        }
    }
}