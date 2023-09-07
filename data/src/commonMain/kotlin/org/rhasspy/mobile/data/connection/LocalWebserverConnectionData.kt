package org.rhasspy.mobile.data.connection

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.data.toStringOrEmpty

@Stable
@Serializable
data class LocalWebserverConnectionData(
    val isEnabled: Boolean,
    val port: Int?,
    val isSSLEnabled: Boolean,
    val keyStoreFile: String?,
    val keyStorePassword: String,
    val keyAlias: String,
    val keyPassword: String
) {

    val portText: String = port.toStringOrEmpty()

}