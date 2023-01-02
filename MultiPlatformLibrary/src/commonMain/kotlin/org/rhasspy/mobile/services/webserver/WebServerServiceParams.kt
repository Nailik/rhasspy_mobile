package org.rhasspy.mobile.services.webserver

import org.rhasspy.mobile.settings.ConfigurationSetting

data class WebServerServiceParams(
    val isHttpServerEnabled: Boolean = ConfigurationSetting.isHttpServerEnabled.value,
    val httpServerPort: Int = ConfigurationSetting.httpServerPort.value,
    val isHttpServerSSLEnabled: Boolean = ConfigurationSetting.isHttpServerSSLEnabledEnabled.value,
    val httpServerSSLKeyStoreFile: String? = ConfigurationSetting.httpServerSSLKeyStoreFile.value,
    val httpServerSSLKeyStorePassword: String = ConfigurationSetting.httpServerSSLKeyStorePassword.value,
    val httpServerSSLKeyAlias: String = ConfigurationSetting.httpServerSSLKeyAlias.value,
    val httpServerSSLKeyPassword: String = ConfigurationSetting.httpServerSSLKeyPassword.value
)