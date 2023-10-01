package org.rhasspy.mobile.viewmodel.configuration.connections.webserver

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.data.toStringOrEmpty
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState.IConfigurationData

@Stable
data class WebServerConnectionConfigurationViewState internal constructor(
    val editData: WebServerConnectionConfigurationData,
    val connectionState: StateFlow<ConnectionState>,
) {

    @Stable
    data class WebServerConnectionConfigurationData internal constructor(
        val isEnabled: Boolean,
        val port: Int?,
        val isSSLEnabled: Boolean,
        val keyStoreFile: String?,
        val keyStorePassword: String,
        val keyAlias: String,
        val keyPassword: String,
    ) : IConfigurationData {

        val portText: String = port.toStringOrEmpty()

    }

}


