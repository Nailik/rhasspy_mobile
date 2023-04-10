package org.rhasspy.mobile.viewmodel.configuration.webserver

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import okio.Path
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.ServiceStateHeaderViewState

@Stable
data class WebServerConfigurationViewState(
    val isHttpServerEnabled: Boolean,
   val httpServerPort: Int,
    val httpServerPortText: String,
    val isHttpServerSSLEnabled: Boolean,
    val httpServerSSLKeyStoreFile: Path?,
    val httpServerSSLKeyStorePassword: String,
    val httpServerSSLKeyAlias: String,
    val httpServerSSLKeyPassword: String
): IConfigurationContentViewState() {

    companion object {
        fun getInitial() = WebServerConfigurationViewState(
            isHttpServerEnabled= ConfigurationSetting.isHttpServerEnabled.value,
            httpServerPort = ConfigurationSetting.httpServerPort.value,
            httpServerPortText = ConfigurationSetting.httpServerPort.value.toString(),
            isHttpServerSSLEnabled = ConfigurationSetting.isHttpServerSSLEnabledEnabled.value,
            httpServerSSLKeyStoreFile = ConfigurationSetting.httpServerSSLKeyStoreFile.value,
            httpServerSSLKeyStorePassword = ConfigurationSetting.httpServerSSLKeyStorePassword.value,
            httpServerSSLKeyAlias = ConfigurationSetting.httpServerSSLKeyAlias.value,
            httpServerSSLKeyPassword = ConfigurationSetting.httpServerSSLKeyPassword.value
        )
    }

    override fun getEditViewState(serviceViewState: StateFlow<ServiceStateHeaderViewState>): IConfigurationEditViewState {
        return IConfigurationEditViewState(
            hasUnsavedChanges = !(isHttpServerEnabled == ConfigurationSetting.isHttpServerEnabled.value &&
            httpServerPort ==ConfigurationSetting.httpServerPort.value &&
            isHttpServerSSLEnabled == ConfigurationSetting.isHttpServerSSLEnabledEnabled.value &&
            httpServerSSLKeyStoreFile == ConfigurationSetting.httpServerSSLKeyStoreFile.value &&
            httpServerSSLKeyStorePassword ==ConfigurationSetting.httpServerSSLKeyStorePassword.value &&
            httpServerSSLKeyAlias ==ConfigurationSetting.httpServerSSLKeyAlias.value &&
            httpServerSSLKeyPassword == ConfigurationSetting.httpServerSSLKeyPassword.value),
            isTestingEnabled = isHttpServerEnabled,
            serviceViewState = serviceViewState
        )
    }

    override fun save() {
        ConfigurationSetting.isHttpServerEnabled.value = isHttpServerEnabled
        ConfigurationSetting.httpServerPort.value = httpServerPort
        ConfigurationSetting.isHttpServerSSLEnabledEnabled.value = isHttpServerSSLEnabled
        ConfigurationSetting.httpServerSSLKeyStoreFile.value = httpServerSSLKeyStoreFile
        ConfigurationSetting.httpServerSSLKeyStorePassword.value = httpServerSSLKeyStorePassword
        ConfigurationSetting.httpServerSSLKeyAlias.value = httpServerSSLKeyAlias
        ConfigurationSetting.httpServerSSLKeyPassword.value = httpServerSSLKeyPassword
    }

}