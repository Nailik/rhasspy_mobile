package org.rhasspy.mobile.data.connection

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.data.toStringOrEmpty

@Stable
@Serializable
data class MqttConnectionData(
    val enabled: Boolean,
    val host: String,
    val userName: String,
    val password: String,
    val sslEnabled: Boolean,
    val connectionTimeout: Int?,
    val keepAliveInterval: Int?,
    val retryInterval: Long?,
    val keystoreFile: String?
) {

    val connectionTimeoutText: String = connectionTimeout.toStringOrEmpty()
    val keepAliveIntervalText: String = keepAliveInterval.toStringOrEmpty()
    val retryIntervalText: String = retryInterval.toStringOrEmpty()

}