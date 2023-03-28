package org.rhasspy.mobile.logic.services.webserver

import okio.Path
import org.rhasspy.mobile.logic.settings.ConfigurationSetting

data class WebServerServiceParams(
    val isHttpServerEnabled: Boolean = ConfigurationSetting.isHttpServerEnabled.value,
    val httpServerPort: Int = ConfigurationSetting.httpServerPort.value,
    val isHttpServerSSLEnabled: Boolean = ConfigurationSetting.isHttpServerSSLEnabledEnabled.value,
    val httpServerSSLKeyStoreFile: Path? = ConfigurationSetting.httpServerSSLKeyStoreFile.value,
    val httpServerSSLKeyStorePassword: String = ConfigurationSetting.httpServerSSLKeyStorePassword.value,
    val httpServerSSLKeyAlias: String = ConfigurationSetting.httpServerSSLKeyAlias.value,
    val httpServerSSLKeyPassword: String = ConfigurationSetting.httpServerSSLKeyPassword.value
)