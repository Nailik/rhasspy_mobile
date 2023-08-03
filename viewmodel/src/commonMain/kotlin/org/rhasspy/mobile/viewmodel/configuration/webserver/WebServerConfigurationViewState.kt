package org.rhasspy.mobile.viewmodel.configuration.webserver

import androidx.compose.runtime.Stable
import okio.Path
import org.rhasspy.mobile.platformspecific.toStringOrEmpty
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class WebServerConfigurationViewState internal constructor(
    override val editData: WebServerConfigurationData
) : IConfigurationViewState {

    @Stable
    data class WebServerConfigurationData internal constructor(
        val isHttpServerEnabled: Boolean = ConfigurationSetting.isHttpServerEnabled.value,
        val httpServerPort: Int? = ConfigurationSetting.httpServerPort.value,
        val isHttpServerSSLEnabled: Boolean = ConfigurationSetting.isHttpServerSSLEnabledEnabled.value,
        val httpServerSSLKeyStoreFile: Path? = ConfigurationSetting.httpServerSSLKeyStoreFile.value,
        val httpServerSSLKeyStorePassword: String = ConfigurationSetting.httpServerSSLKeyStorePassword.value,
        val httpServerSSLKeyAlias: String = ConfigurationSetting.httpServerSSLKeyAlias.value,
        val httpServerSSLKeyPassword: String = ConfigurationSetting.httpServerSSLKeyPassword.value,
    ) : IConfigurationData {

        val httpServerPortText: String = httpServerPort.toStringOrEmpty()
        val httpServerSSLKeyStoreFileName: String? = httpServerSSLKeyStoreFile?.name

    }

}


