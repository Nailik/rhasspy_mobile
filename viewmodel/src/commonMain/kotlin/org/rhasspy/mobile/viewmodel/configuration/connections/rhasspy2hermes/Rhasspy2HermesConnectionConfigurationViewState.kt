package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.data.toStringOrEmpty
import org.rhasspy.mobile.data.service.ConnectionState

@Stable
data class Rhasspy2HermesConnectionConfigurationViewState internal constructor(
    val editData: Rhasspy2HermesConnectionConfigurationData,
    val connectionState: StateFlow<ConnectionState>,
)  {

    @Stable
    data class Rhasspy2HermesConnectionConfigurationData internal constructor(
        val host: String,
        val timeout: Long?,
        val bearerToken: String,
        val isSSLVerificationDisabled: Boolean,
    ) {

        val timeoutText: String = timeout.toStringOrEmpty()

    }

}