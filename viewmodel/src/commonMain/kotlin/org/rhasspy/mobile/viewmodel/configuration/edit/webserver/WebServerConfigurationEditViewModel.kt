package org.rhasspy.mobile.viewmodel.configuration.edit.webserver

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.utils.OpenLinkUtils
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.ScreenViewState.SnackBarViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationViewState.WebServerConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.WebServerConfigurationScreenDestination.TestScreen
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
class WebServerConfigurationEditViewModel(
    service: WebServerService,
    viewStateCreator: WebServerConfigurationEditViewStateCreator
) : IConfigurationEditViewModel<WebServerConfigurationData>(
    testPageDestination = TestScreen
) {

    override val _configurationEditViewState = MutableStateFlow(ConfigurationEditViewState(serviceViewState = ServiceViewState(service.serviceState)))

    private val _editData = MutableStateFlow(WebServerConfigurationData())

    val viewState = viewStateCreator.combine(_editData, _screenViewState, _configurationEditViewState)

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
                is UpdateHttpServerPort -> it.copy(httpServerPortText = change.value)
                is SetHttpServerSSLKeyStoreFile -> it.copy(httpServerSSLKeyStoreFile = change.value)
            }
        }
    }


    private fun onAction(action: Action) {
        when (action) {
            OpenWebServerSSLWiki -> openWebServerSSLWiki()
            SelectSSLCertificate -> selectSSLCertificate()
            BackClick -> navigator.onBackPressed()
        }
    }

    private fun openWebServerSSLWiki() {
        if (!get<OpenLinkUtils>().openLink(LinkType.WikiWebServerSSL)) {
            _screenViewState.update {
                it.copy(snackBarViewState = SnackBarViewState(text = MR.strings.linkOpenFailed.stable))
            }
        }
    }

    //open file chooser to select certificate
    private fun selectSSLCertificate() {
        viewModelScope.launch {
            FileUtils.selectFile(FolderType.CertificateFolder.WebServer)?.also { path ->
                onEvent(SetHttpServerSSLKeyStoreFile(path))
            } ?: run {
                _screenViewState.update {
                    it.copy(snackBarViewState = SnackBarViewState(text = MR.strings.selectFileFailed.stable))
                }
            }
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
            ConfigurationSetting.httpServerPort.value = httpServerPort
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