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
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.utils.OpenLinkUtils
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.WebServerConfigurationScreenDestination.TestScreen
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
class WebServerConfigurationEditViewModel(
    service: WebServerService,
    viewStateCreator: WebServerConfigurationViewStateCreator
) : IConfigurationEditViewModel(TestScreen) {

    override val configurationEditViewState: MutableStateFlow<ConfigurationEditViewState> =
        MutableStateFlow(ConfigurationEditViewState(ServiceViewState(service.serviceState)))

    private val _viewState = viewStateCreator()
    val viewState = _viewState.readOnly

    fun onEvent(event: WebServerConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
            is Consumed -> onConsumed(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(
                editData = with(it.editData) {
                    when (change) {
                        is SetHttpServerEnabled -> copy(isHttpServerEnabled = change.value)
                        is SetHttpServerSSLEnabled -> copy(isHttpServerSSLEnabled = change.value)
                        is UpdateHttpSSLKeyAlias -> copy(httpServerSSLKeyAlias = change.value)
                        is UpdateHttpSSLKeyPassword -> copy(httpServerSSLKeyPassword = change.value)
                        is UpdateHttpSSLKeyStorePassword -> copy(httpServerSSLKeyStorePassword = change.value)
                        is UpdateHttpServerPort -> copy(httpServerPortText = change.value)
                        is SetHttpServerSSLKeyStoreFile -> copy(httpServerSSLKeyStoreFile = change.value)
                    }
                }
            )
        }
    }


    private fun onAction(action: Action) {
        when (action) {
            OpenWebServerSSLWiki -> openWebServerSSLWiki()
            SelectSSLCertificate -> selectSSLCertificate()
            BackClick -> navigator.onBackPressed()
        }
    }

    private fun onConsumed(consumed: Consumed) {
        _viewState.update {
            when (consumed) {
                is ShowSnackBar -> it.copy(snackBarText = null)
            }
        }
    }

    private fun openWebServerSSLWiki() {
        if (!get<OpenLinkUtils>().openLink(LinkType.WikiWebServerSSL)) {
            _viewState.update {
                it.copy(snackBarText = MR.strings.linkOpenFailed.stable)
            }
        }
    }

    //open file chooser to select certificate
    private fun selectSSLCertificate() {
        viewModelScope.launch {
            FileUtils.selectFile(FolderType.CertificateFolder.WebServer)?.also { path ->
                onEvent(SetHttpServerSSLKeyStoreFile(path))
            } ?: run {
                _viewState.update {
                    it.copy(snackBarText = MR.strings.selectFileFailed.stable)
                }
            }
        }
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
        with(_viewState.value.editData) {
            //delete new keystore file if changed
            if (httpServerSSLKeyStoreFile != ConfigurationSetting.httpServerSSLKeyStoreFile.value) {
                httpServerSSLKeyStoreFile?.commonDelete()
            }
        }
    }

}