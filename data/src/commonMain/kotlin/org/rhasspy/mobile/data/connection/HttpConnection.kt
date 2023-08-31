package org.rhasspy.mobile.data.connection

import androidx.compose.runtime.Stable

@Stable
data class HttpConnection(
    val id: Long = 5,
    val host: String = "host",
    val port: Int? = 5,
    val timeout: Long? = null,
    val isUseBearer: Boolean = false,
    val bearerToken: String? = "",
    val isHermes: Boolean = false,
    val isWyoming: Boolean = false,
    val isHomeAssistant: Boolean = false,
    val isSslVerificationDisabled: Boolean = false
)