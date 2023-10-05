package org.rhasspy.mobile.viewmodel.configuration.connections.webserver

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.ConnectionState

@Stable
data class WebServerConnectionConfigurationViewState internal constructor(
    val editData: WebServerConnectionConfigurationData,
    val connectionState: StateFlow<ConnectionState>,
) {

    @Stable
    data class WebServerConnectionConfigurationData internal constructor(
        val isEnabled: Boolean,
        val port: String,
        val isSSLEnabled: Boolean,
        val keyStoreFile: String?,
        val keyStorePassword: String,
        val keyAlias: String,
        val keyPassword: String,
    )

}


