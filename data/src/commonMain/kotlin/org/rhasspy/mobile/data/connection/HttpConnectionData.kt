package org.rhasspy.mobile.data.connection

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.data.toStringOrEmpty

@Stable
@Serializable
data class HttpConnectionData(
    val host: String,
    val timeout: Long?,
    val bearerToken: String,
    val isSSLVerificationDisabled: Boolean
) {

    val timeoutText: String = timeout.toStringOrEmpty()

}