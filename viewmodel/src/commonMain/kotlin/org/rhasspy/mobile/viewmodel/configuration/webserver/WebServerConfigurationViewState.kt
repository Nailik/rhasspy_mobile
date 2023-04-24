package org.rhasspy.mobile.viewmodel.configuration.webserver

import androidx.compose.runtime.Stable
import okio.Path
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState

@Stable
data class WebServerConfigurationViewState internal constructor(
    val isHttpServerEnabled: Boolean = ConfigurationSetting.isHttpServerEnabled.value,
    val httpServerPortText: String = ConfigurationSetting.httpServerPort.value.toString(),
    val isHttpServerSSLEnabled: Boolean = ConfigurationSetting.isHttpServerSSLEnabledEnabled.value,
    val httpServerSSLKeyStoreFile: Path? = ConfigurationSetting.httpServerSSLKeyStoreFile.value,
    val httpServerSSLKeyStorePassword: String = ConfigurationSetting.httpServerSSLKeyStorePassword.value,
    val httpServerSSLKeyAlias: String = ConfigurationSetting.httpServerSSLKeyAlias.value,
    val httpServerSSLKeyPassword: String = ConfigurationSetting.httpServerSSLKeyPassword.value
) : IConfigurationEditViewState() {

    val httpServerPort: Int get() = httpServerPortText.toIntOrZero()

    val httpServerSSLKeyStoreFileName: String? get() = httpServerSSLKeyStoreFile?.name

    override val isTestingEnabled: Boolean get() = isHttpServerEnabled


}