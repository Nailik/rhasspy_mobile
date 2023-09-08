package org.rhasspy.mobile.data.connection

import kotlinx.serialization.Serializable

@Serializable
data class MqttConnectionData(
    val isEnabled: Boolean,
    val host: String,
    val userName: String,
    val password: String,
    val isSSLEnabled: Boolean,
    val connectionTimeout: Int,
    val keepAliveInterval: Int,
    val retryInterval: Long,
    val keystoreFile: String?
)