package org.rhasspy.mobile.viewmodel.configuration.edit.webserver

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
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationViewState.WebServerConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.WebServerConfigurationScreenDestination.TestScreen

@Stable
class WebServerConfigurationEditViewModel(
    service: WebServerService,
    private val viewStateCreator: ConfigurationEditViewStateCreator
) : IConfigurationEditViewModel(
    testPageDestination = TestScreen,
    service = service
) {

    private val initialConfigurationData = WebServerConfigurationData()

    private val _editData = MutableStateFlow(initialConfigurationData)
    private val _viewState = MutableStateFlow(WebServerConfigurationViewState(initialConfigurationData))
    val viewState = combineState(_viewState, _editData) { viewState, editData ->
        viewState.copy(editData = editData)
    }

    override fun initViewStateCreator(
        configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>
    ): StateFlow<ConfigurationEditViewState> {
        return viewStateCreator(
            init = ::WebServerConfigurationData,
            isTestingEnabled = { it.isHttpServerEnabled },
            editData = _editData,
            configurationEditViewState = configurationEditViewState
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

