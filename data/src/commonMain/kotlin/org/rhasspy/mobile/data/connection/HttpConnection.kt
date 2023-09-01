package org.rhasspy.mobile.data.connection

import androidx.compose.runtime.Stable

@Stable
data class HttpConnection(
    val id: Long,
    val host: String,
    val port: Int?,
    val timeout: Long?,
    val bearerToken: String?,
    val isHermes: Boolean,
    val isWyoming: Boolean,
    val isHomeAssistant: Boolean,
    val isSslVerificationDisabled: Boolean
)