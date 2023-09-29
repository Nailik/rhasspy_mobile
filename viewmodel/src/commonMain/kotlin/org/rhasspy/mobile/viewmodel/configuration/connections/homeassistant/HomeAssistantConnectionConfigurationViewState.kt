package org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.data.toStringOrEmpty
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState

@Stable
data class HomeAssistantConnectionConfigurationViewState internal constructor(
    override val editData: HomeAssistantConnectionConfigurationData
) : IConfigurationViewState {

    @Stable
    data class HomeAssistantConnectionConfigurationData internal constructor(
        val host: String,
        val timeout: Long?,
        val bearerToken: String,
        val isSSLVerificationDisabled: Boolean,
    ) {

        val timeoutText: String = timeout.toStringOrEmpty()

    }

}