package org.rhasspy.mobile.data.connection

import androidx.compose.runtime.Stable

@Stable
data class HttpConnectionParams(
    val id: Long?,
    val host: String,
    val timeout: Long?,
    val bearerToken: String?,
    val isSSLVerificationDisabled: Boolean
)