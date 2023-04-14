package org.rhasspy.mobile.viewmodel.configuration.webserver

import androidx.compose.runtime.Stable
import okio.Path
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState

@Stable
data class WebServerConfigurationViewState(
    val isHttpServerEnabled: Boolean= ConfigurationSetting.isHttpServerEnabled.value,
    val httpServerPortText: String= ConfigurationSetting.httpServerPort.value.toString(),
    val isHttpServerSSLEnabled: Boolean= ConfigurationSetting.isHttpServerSSLEnabledEnabled.value,
    val httpServerSSLKeyStoreFile: Path?= ConfigurationSetting.httpServerSSLKeyStoreFile.value,
    val httpServerSSLKeyStorePassword: String= ConfigurationSetting.httpServerSSLKeyStorePassword.value,
    val httpServerSSLKeyAlias: String = ConfigurationSetting.httpServerSSLKeyAlias.value,
    val httpServerSSLKeyPassword: String= ConfigurationSetting.httpServerSSLKeyPassword.value
): IConfigurationEditViewState {

    override val hasUnsavedChanges: Boolean
        get() =!(isHttpServerEnabled == ConfigurationSetting.isHttpServerEnabled.value &&
                httpServerPort == ConfigurationSetting.httpServerPort.value &&
                isHttpServerSSLEnabled == ConfigurationSetting.isHttpServerSSLEnabledEnabled.value &&
                httpServerSSLKeyStoreFile == ConfigurationSetting.httpServerSSLKeyStoreFile.value &&
                httpServerSSLKeyStorePassword == ConfigurationSetting.httpServerSSLKeyStorePassword.value &&
                httpServerSSLKeyAlias == ConfigurationSetting.httpServerSSLKeyAlias.value &&
                httpServerSSLKeyPassword == ConfigurationSetting.httpServerSSLKeyPassword.value)

    override val isTestingEnabled: Boolean get() = isHttpServerEnabled

    val httpServerPort: Int get() = httpServerPortText.toIntOrZero()

}