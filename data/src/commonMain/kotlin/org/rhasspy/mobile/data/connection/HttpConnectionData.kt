package org.rhasspy.mobile.data.connection

import kotlinx.serialization.Serializable

@Serializable
data class HttpConnectionData(
    val host: String,
    val timeout: Long?,
    val bearerToken: String,
    val isSSLVerificationDisabled: Boolean
)