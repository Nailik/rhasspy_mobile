package org.rhasspy.mobile.logic.services.webserver

import okio.Path

data class WebServerServiceParams(
    val isHttpServerEnabled: Boolean,
    val httpServerPort: Int,
    val isHttpServerSSLEnabled: Boolean,
    val httpServerSSLKeyStoreFile: Path?,
    val httpServerSSLKeyStorePassword: String,
    val httpServerSSLKeyAlias: String,
    val httpServerSSLKeyPassword: String
)