package org.rhasspy.mobile.services.webserver

import org.rhasspy.mobile.settings.ConfigurationSettings

data class WebServerServiceParams(
    val isHttpServerEnabled: Boolean = ConfigurationSettings.isHttpServerEnabled.value,
    val httpServerPort: Int = ConfigurationSettings.httpServerPort.value,
    val isHttpServerSSLEnabled: Boolean = ConfigurationSettings.isHttpServerSSLEnabled.value
)