package org.rhasspy.mobile.viewmodel.configuration.edit.webserver

import androidx.compose.runtime.Stable
import okio.Path
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.ScreenViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState

@Stable
data class WebServerConfigurationViewState internal constructor(
    val editData: WebServerConfigurationData,
    val screenViewState: ScreenViewState,
    val editViewState: ConfigurationEditViewState,
) {

    @Stable
    data class WebServerConfigurationData internal constructor(
        val isHttpServerEnabled: Boolean = ConfigurationSetting.isHttpServerEnabled.value,
        val httpServerPortText: String = ConfigurationSetting.httpServerPort.value.toString(),
        val isHttpServerSSLEnabled: Boolean = ConfigurationSetting.isHttpServerSSLEnabledEnabled.value,
        val httpServerSSLKeyStoreFile: Path? = ConfigurationSetting.httpServerSSLKeyStoreFile.value,
        val httpServerSSLKeyStorePassword: String = ConfigurationSetting.httpServerSSLKeyStorePassword.value,
        val httpServerSSLKeyAlias: String = ConfigurationSetting.httpServerSSLKeyAlias.value,
        val httpServerSSLKeyPassword: String = ConfigurationSetting.httpServerSSLKeyPassword.value,
    ) {

        val httpServerPort: Int get() = httpServerPortText.toIntOrZero()
        val httpServerSSLKeyStoreFileName: String? get() = httpServerSSLKeyStoreFile?.name

    }

}


