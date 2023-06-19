package org.rhasspy.mobile.viewmodel.configuration.edit.webserver

import androidx.compose.runtime.Stable
import okio.Path
import org.rhasspy.mobile.settings.ConfigurationSetting

@Stable
data class WebServerConfigurationViewState internal constructor(
    val editData: WebServerConfigurationData
) {

    @Stable
    data class WebServerConfigurationData internal constructor(
        val isHttpServerEnabled: Boolean = ConfigurationSetting.isHttpServerEnabled.value,
        val httpServerPort: Int? = ConfigurationSetting.httpServerPort.value,
        val isHttpServerSSLEnabled: Boolean = ConfigurationSetting.isHttpServerSSLEnabledEnabled.value,
        val httpServerSSLKeyStoreFile: Path? = ConfigurationSetting.httpServerSSLKeyStoreFile.value,
        val httpServerSSLKeyStorePassword: String = ConfigurationSetting.httpServerSSLKeyStorePassword.value,
        val httpServerSSLKeyAlias: String = ConfigurationSetting.httpServerSSLKeyAlias.value,
        val httpServerSSLKeyPassword: String = ConfigurationSetting.httpServerSSLKeyPassword.value,
    ) {

        val httpServerPortText: String = httpServerPort.toString()
        val httpServerSSLKeyStoreFileName: String? = httpServerSSLKeyStoreFile?.name

    }

}


