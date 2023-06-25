package org.rhasspy.mobile.viewmodel.configuration.webserver

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationViewState.WebServerConfigurationData

@Stable
class WebServerConfigurationViewModel(
    service: WebServerService
) : IConfigurationViewModel(
    service = service
) {

    private val initialConfigurationData = WebServerConfigurationData()

    private val _editData = MutableStateFlow(initialConfigurationData)
    private val _viewState = MutableStateFlow(WebServerConfigurationViewState(initialConfigurationData))
    val viewState = combineState(_viewState, _editData) { viewState, editData ->
        viewState.copy(editData = editData)
    }

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<IConfigurationViewState>
    ): StateFlow<IConfigurationViewState> {
        return viewStateCreator(
            init = ::WebServerConfigurationData,
            editData = _editData,
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
        _editData.update {
            when (change) {
                is SetHttpServerEnabled -> it.copy(isHttpServerEnabled = change.value)
                is SetHttpServerSSLEnabled -> it.copy(isHttpServerSSLEnabled = change.value)
                is UpdateHttpSSLKeyAlias -> it.copy(httpServerSSLKeyAlias = change.value)
                is UpdateHttpSSLKeyPassword -> it.copy(httpServerSSLKeyPassword = change.value)
                is UpdateHttpSSLKeyStorePassword -> it.copy(httpServerSSLKeyStorePassword = change.value)
                is UpdateHttpServerPort -> it.copy(httpServerPort = change.value.toIntOrNull())
                is SetHttpServerSSLKeyStoreFile -> it.copy(httpServerSSLKeyStoreFile = change.value)
            }
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
     * save data configuration
     */
    override fun onSave() {
        with(_editData.value) {
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

    /**
     * undo all changes
     */
    override fun onDiscard() {
        with(_editData.value) {
            //delete new keystore file if changed
            if (httpServerSSLKeyStoreFile != ConfigurationSetting.httpServerSSLKeyStoreFile.value) {
                httpServerSSLKeyStoreFile?.commonDelete()
            }
        }
    }

}

