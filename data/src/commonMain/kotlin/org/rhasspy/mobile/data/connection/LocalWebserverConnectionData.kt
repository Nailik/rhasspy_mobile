package org.rhasspy.mobile.data.connection

import kotlinx.serialization.Serializable

@Serializable
data class LocalWebserverConnectionData(
    val isEnabled: Boolean,
    val port: Int,
    val isSSLEnabled: Boolean,
    val keyStoreFile: String?,
    val keyStorePassword: String,
    val keyAlias: String,
    val keyPassword: String
)