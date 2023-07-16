package org.rhasspy.mobile.viewmodel.configuration.webserver

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.logic.services.webserver.IWebServerService
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.toIntOrNullOrConstant
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationViewState.WebServerConfigurationData

@Stable
class WebServerConfigurationViewModel(
    service: IWebServerService
) : ConfigurationViewModel(
    service = service
) {

    private val _viewState = MutableStateFlow(WebServerConfigurationViewState(WebServerConfigurationData()))
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::WebServerConfigurationData,
            viewState = viewState,
            configurationViewState = configurationViewState
        )
    }

    fun onEvent(event: WebServerConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SetHttpServerEnabled          -> copy(isHttpServerEnabled = change.value)
                    is SetHttpServerSSLEnabled       -> copy(isHttpServerSSLEnabled = change.value)
                    is UpdateHttpSSLKeyAlias         -> copy(httpServerSSLKeyAlias = change.value)
                    is UpdateHttpSSLKeyPassword      -> copy(httpServerSSLKeyPassword = change.value)
                    is UpdateHttpSSLKeyStorePassword -> copy(httpServerSSLKeyStorePassword = change.value)
                    is UpdateHttpServerPort          -> copy(httpServerPort = change.value.toIntOrNullOrConstant())
                    is SetHttpServerSSLKeyStoreFile  -> copy(httpServerSSLKeyStoreFile = change.value)
                }
            })
        }
    }


    private fun onAction(action: Action) {
        when (action) {
            OpenWebServerSSLWiki -> openLink(LinkType.WikiWebServerSSL)
            SelectSSLCertificate -> selectFile(FolderType.CertificateFolder.WebServer) { path ->
                onEvent(SetHttpServerSSLKeyStoreFile(path))
            }

            BackClick -> navigator.onBackPressed()
        }
    }

    /**
     * undo all changes
     */
    override fun onDiscard() {
        with(_viewState.value.editData) {
            //delete new keystore file if changed
            if (httpServerSSLKeyStoreFile != ConfigurationSetting.httpServerSSLKeyStoreFile.value) {
                httpServerSSLKeyStoreFile?.commonDelete()
            }
        }
        _viewState.update { it.copy(editData = WebServerConfigurationData()) }
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        with(_viewState.value.editData) {
            //delete old keystore file if changed
            if (httpServerSSLKeyStoreFile != ConfigurationSetting.httpServerSSLKeyStoreFile.value) {
                ConfigurationSetting.httpServerSSLKeyStoreFile.value?.commonDelete()
            }

            ConfigurationSetting.isHttpServerEnabled.value = isHttpServerEnabled
            ConfigurationSetting.httpServerPort.value = httpServerPort.toIntOrZero()
            ConfigurationSetting.isHttpServerSSLEnabledEnabled.value = isHttpServerSSLEnabled
            ConfigurationSetting.httpServerSSLKeyStoreFile.value = httpServerSSLKeyStoreFile
            ConfigurationSetting.httpServerSSLKeyStorePassword.value = httpServerSSLKeyStorePassword
            ConfigurationSetting.httpServerSSLKeyAlias.value = httpServerSSLKeyAlias
            ConfigurationSetting.httpServerSSLKeyPassword.value = httpServerSSLKeyPassword
        }
    }

}

