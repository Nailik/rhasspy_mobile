package org.rhasspy.mobile.data.connection

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class HttpConnectionData(
    val host: String,
    val timeout: Duration,
    val bearerToken: String,
    val isSSLVerificationDisabled: Boolean
)