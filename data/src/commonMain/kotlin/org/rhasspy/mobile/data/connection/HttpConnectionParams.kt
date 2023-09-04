package org.rhasspy.mobile.data.connection

import androidx.compose.runtime.Stable

@Stable
data class HttpConnectionParams(
    val id: Long?,
    val host: String,
    val port: Int?, //TODO remove port -> all in url
    val timeout: Long?,
    val bearerToken: String?,
    val isHermes: Boolean,
    val isWyoming: Boolean,
    val isHomeAssistant: Boolean,
    val isSSLVerificationDisabled: Boolean
)