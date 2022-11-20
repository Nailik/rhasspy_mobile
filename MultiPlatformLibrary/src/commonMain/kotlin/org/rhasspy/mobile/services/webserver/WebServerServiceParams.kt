package org.rhasspy.mobile.services.webserver

data class WebServerServiceParams(
    val isHttpServerEnabled: Boolean,
    val httpServerPort: Int,
    val isHttpServerSSLEnabled: Boolean
)