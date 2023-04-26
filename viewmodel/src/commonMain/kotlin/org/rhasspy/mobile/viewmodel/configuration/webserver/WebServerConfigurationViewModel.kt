package org.rhasspy.mobile.viewmodel.configuration.webserver

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Action.OpenWebServerSSLWiki
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Action.SelectSSLCertificate
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.utils.OpenLinkUtils

@Stable
class WebServerConfigurationViewModel(
    service: WebServerService
) : IConfigurationViewModel<WebServerConfigurationViewState>(
    service = service,
    initialViewState = ::WebServerConfigurationViewState
) {

    fun onEvent(event: WebServerConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
            is Consumed -> onConsumed(event)
        }
    }

    private fun onChange(change: Change) {
        contentViewState.update {
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
        }
    }

    private fun onConsumed(consumed: Consumed) {
        contentViewState.update {
            when (consumed) {
                is ShowSnackBar -> it.copy(snackBarText = null)
            }
        }
    }

    private fun openWebServerSSLWiki() {
        if (!OpenLinkUtils.openLink(LinkType.WikiWebServerSSL)) {
            contentViewState.update {
                it.copy(snackBarText = MR.strings.linkOpenFailed.stable)
            }
        }
    }

    //open file chooser to select certificate
    private fun selectSSLCertificate() {
        viewModelScope.launch {
            FileUtils.selectFile(FolderType.CertificateFolder.WebServer)?.also { path ->
                onEvent(SetHttpServerSSLKeyStoreFile(path))
            } ?: {
                contentViewState.update {
                    it.copy(snackBarText = MR.strings.selectFileFailed.stable)
                }
            }
        }
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        //delete old keystore file if changed
        if (data.httpServerSSLKeyStoreFile != ConfigurationSetting.httpServerSSLKeyStoreFile.value) {
            ConfigurationSetting.httpServerSSLKeyStoreFile.value?.commonDelete()
        }

        ConfigurationSetting.isHttpServerEnabled.value = data.isHttpServerEnabled
        ConfigurationSetting.httpServerPort.value = data.httpServerPort
        ConfigurationSetting.isHttpServerSSLEnabledEnabled.value = data.isHttpServerSSLEnabled
        ConfigurationSetting.httpServerSSLKeyStoreFile.value = data.httpServerSSLKeyStoreFile
        ConfigurationSetting.httpServerSSLKeyStorePassword.value = data.httpServerSSLKeyStorePassword
        ConfigurationSetting.httpServerSSLKeyAlias.value = data.httpServerSSLKeyAlias
        ConfigurationSetting.httpServerSSLKeyPassword.value = data.httpServerSSLKeyPassword
    }

    /**
     * undo all changes
     */
    override fun onDiscard() {
        //delete new keystore file if changed
        if (data.httpServerSSLKeyStoreFile != ConfigurationSetting.httpServerSSLKeyStoreFile.value) {
            data.httpServerSSLKeyStoreFile?.commonDelete()
        }
    }

}