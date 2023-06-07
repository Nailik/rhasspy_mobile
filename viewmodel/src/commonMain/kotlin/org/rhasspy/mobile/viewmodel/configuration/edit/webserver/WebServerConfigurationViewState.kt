package org.rhasspy.mobile.viewmodel.configuration.edit.webserver

import androidx.compose.runtime.Stable
import okio.Path
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewState

@Stable
data class WebServerConfigurationViewState internal constructor(
    val isHttpServerEnabled: Boolean = ConfigurationSetting.isHttpServerEnabled.value,
    val httpServerPortText: String = ConfigurationSetting.httpServerPort.value.toString(),
    val isHttpServerSSLEnabled: Boolean = ConfigurationSetting.isHttpServerSSLEnabledEnabled.value,
    val httpServerSSLKeyStoreFile: Path? = ConfigurationSetting.httpServerSSLKeyStoreFile.value,
    val httpServerSSLKeyStorePassword: String = ConfigurationSetting.httpServerSSLKeyStorePassword.value,
    val httpServerSSLKeyAlias: String = ConfigurationSetting.httpServerSSLKeyAlias.value,
    val httpServerSSLKeyPassword: String = ConfigurationSetting.httpServerSSLKeyPassword.value,
    val snackBarText: StableStringResource? = null
) : IConfigurationEditViewState() {

    val httpServerPort: Int get() = httpServerPortText.toIntOrZero()

    val httpServerSSLKeyStoreFileName: String? get() = httpServerSSLKeyStoreFile?.name

    override val isTestingEnabled: Boolean get() = isHttpServerEnabled


}