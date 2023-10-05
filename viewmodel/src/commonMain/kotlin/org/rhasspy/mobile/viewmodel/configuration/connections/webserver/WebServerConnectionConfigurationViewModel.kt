package org.rhasspy.mobile.viewmodel.configuration.connections.webserver

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import okio.Path
import org.rhasspy.mobile.data.data.takeInt
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalFilePath
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationUiEvent.Action.OpenWebServerSSLWiki
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationUiEvent.Action.SelectSSLCertificate
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class WebServerConnectionConfigurationViewModel(
    private val mapper: WebServerConnectionConfigurationDataMapper,
    private val nativeApplication: NativeApplication,
    webServerConnection: IWebServerConnection
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(
        WebServerConnectionConfigurationViewState(
            editData = mapper(ConfigurationSetting.localWebserverConnection.value),
            connectionState = webServerConnection.connectionState,
        )
    )
    val viewState = _viewState.readOnly

    fun onEvent(event: WebServerConnectionConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SetHttpServerEnabled          -> copy(isEnabled = change.value)
                    is SetHttpServerSSLEnabled       -> copy(isSSLEnabled = change.value)
                    is UpdateHttpSSLKeyAlias         -> copy(keyAlias = change.value)
                    is UpdateHttpSSLKeyPassword      -> copy(keyPassword = change.value)
                    is UpdateHttpSSLKeyStorePassword -> copy(keyStorePassword = change.value)
                    is UpdateHttpServerPort          -> copy(port = change.value.takeInt())
                    is SetHttpServerSSLKeyStoreFile  -> copy(keyStoreFile = "${FolderType.CertificateFolder.WebServer}/${change.value.name}")
                }
            })
        }
        //delete old keystore file if changed
        if (_viewState.value.editData.keyStoreFile != ConfigurationSetting.localWebserverConnection.value.keyStoreFile) {
            ConfigurationSetting.localWebserverConnection.value.keyStoreFile?.also {
                Path.commonInternalFilePath(nativeApplication, it).commonDelete()
            }
        }
        ConfigurationSetting.localWebserverConnection.value = mapper(_viewState.value.editData)
    }


    private fun onAction(action: Action) {
        when (action) {
            OpenWebServerSSLWiki -> openLink(LinkType.WikiWebServerSSL)
            SelectSSLCertificate -> selectFile(FolderType.CertificateFolder.WebServer) { path -> onEvent(SetHttpServerSSLKeyStoreFile(path)) }
        }
    }

}

