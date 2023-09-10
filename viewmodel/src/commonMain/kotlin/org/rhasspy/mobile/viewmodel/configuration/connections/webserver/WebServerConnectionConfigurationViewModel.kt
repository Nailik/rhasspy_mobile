package org.rhasspy.mobile.viewmodel.configuration.connections.webserver

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import okio.Path.Companion.toPath
import org.rhasspy.mobile.data.data.toIntOrNullOrConstant
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationUiEvent.Change.*

@Stable
class WebServerConnectionConfigurationViewModel(
    private val mapper: WebServerConnectionConfigurationDataMapper,
    service: IWebServerConnection
) : ConfigurationViewModel(
    service = null
) {

    private val initialData get() = mapper(ConfigurationSetting.localWebserverConnection.value)
    private val _viewState = MutableStateFlow(WebServerConnectionConfigurationViewState(initialData))
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
                    is UpdateHttpServerPort          -> copy(port = change.value.toIntOrNullOrConstant())
                    is SetHttpServerSSLKeyStoreFile  -> copy(keyStoreFile = change.value.name)
                }
            })
        }
    }


    private fun onAction(action: Action) {
        when (action) {
            OpenWebServerSSLWiki -> openLink(LinkType.WikiWebServerSSL)
            SelectSSLCertificate -> selectFile(FolderType.CertificateFolder.WebServer) { path -> onEvent(SetHttpServerSSLKeyStoreFile(path)) }
            BackClick            -> navigator.onBackPressed()
        }
    }

    /**
     * undo all changes
     */
    override fun onDiscard() {
        //delete new keystore file if changed
        if (_viewState.value.editData.keyStoreFile != ConfigurationSetting.localWebserverConnection.value.keyStoreFile) {
            _viewState.value.editData.keyStoreFile?.toPath()?.commonDelete()
        }
        _viewState.update { it.copy(editData = initialData) }
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        //delete old keystore file if changed
        if (_viewState.value.editData.keyStoreFile != ConfigurationSetting.localWebserverConnection.value.keyStoreFile) {
            ConfigurationSetting.localWebserverConnection.value.keyStoreFile?.toPath()?.commonDelete()
        }

        ConfigurationSetting.localWebserverConnection.value = mapper(_viewState.value.editData)
        _viewState.update { it.copy(editData = initialData) }
    }

}

