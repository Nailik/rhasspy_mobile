package org.rhasspy.mobile.data.connection

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class MqttConnectionData(
    val isEnabled: Boolean,
    val host: String,
    val userName: String,
    val password: String,
    val isSSLEnabled: Boolean,
    val connectionTimeout: Duration,
    val keepAliveInterval: Duration,
    val retryInterval: Duration,
    val keystoreFile: String?
)