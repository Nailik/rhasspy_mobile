package org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.ConnectionState

@Stable
data class HomeAssistantConnectionConfigurationViewState internal constructor(
    val editData: HomeAssistantConnectionConfigurationData,
    val connectionState: StateFlow<ConnectionState>,
) {

    @Stable
    data class HomeAssistantConnectionConfigurationData internal constructor(
        val host: String,
        val timeout: String,
        val bearerToken: String,
        val isSSLVerificationDisabled: Boolean,
    )

}