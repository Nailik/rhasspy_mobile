package org.rhasspy.mobile.viewmodel.configuration.webserver

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logic.openLink
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.logic.services.webserver.WebServerServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change.SetHttpServerEnabled
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change.SetHttpServerSSLEnabled
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change.SetHttpServerSSLKeyStoreFile
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change.UpdateHttpSSLKeyAlias
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change.UpdateHttpSSLKeyPassword
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change.UpdateHttpSSLKeyStorePassword
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Change.UpdateHttpServerPort
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Action.OpenWebServerSSLWiki
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiEvent.Action.SelectSSLCertificate

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
            OpenWebServerSSLWiki -> openLink("https://github.com/Nailik/rhasspy_mobile/wiki/Webserver#enable-ssl")
            SelectSSLCertificate -> selectSSLCertificate()
        }
    }

    //open file chooser to select certificate
    private fun selectSSLCertificate() {
        viewModelScope.launch {
            FileUtils.selectFile(FolderType.CertificateFolder.WebServer)?.also { path ->
                onEvent(SetHttpServerSSLKeyStoreFile(path))
            }
        }
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSetting.apply {
            //delete old keystore file if changed
            if (data.httpServerSSLKeyStoreFile != httpServerSSLKeyStoreFile.value) {
                httpServerSSLKeyStoreFile.value?.commonDelete()
            }

            isHttpServerEnabled.value = data.isHttpServerEnabled
            httpServerPort.value = data.httpServerPort
            isHttpServerSSLEnabledEnabled.value = data.isHttpServerSSLEnabled
            httpServerSSLKeyStoreFile.value = data.httpServerSSLKeyStoreFile
            httpServerSSLKeyStorePassword.value = data.httpServerSSLKeyStorePassword
            httpServerSSLKeyAlias.value = data.httpServerSSLKeyAlias
            httpServerSSLKeyPassword.value = data.httpServerSSLKeyPassword
        }
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

    override fun initializeTestParams() {
        get<WebServerServiceParams> {
            parametersOf(
                WebServerServiceParams(
                    isHttpServerEnabled = data.isHttpServerEnabled,
                    httpServerPort = data.httpServerPort,
                    isHttpServerSSLEnabled = data.isHttpServerSSLEnabled,
                    httpServerSSLKeyStoreFile = data.httpServerSSLKeyStoreFile,
                    httpServerSSLKeyStorePassword = data.httpServerSSLKeyStorePassword,
                    httpServerSSLKeyAlias = data.httpServerSSLKeyAlias,
                    httpServerSSLKeyPassword = data.httpServerSSLKeyPassword
                )
            )
        }
        get<WebServerService>()
    }

}