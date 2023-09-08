package org.rhasspy.mobile.viewmodel.configuration.connections.webserver

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.data.toStringOrEmpty
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class WebServerConnectionConfigurationViewState internal constructor(
    override val editData: WebServerConnectionConfigurationData
) : IConfigurationViewState {

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


