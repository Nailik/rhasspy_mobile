package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.data.toStringOrEmpty
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState.IConfigurationData

@Stable
data class Rhasspy2HermesConnectionConfigurationViewState internal constructor(
    val editData: Rhasspy2HermesConnectionConfigurationData,
    val isCheckConnectionEnabled: Boolean,
    val connectionState: StateFlow<ConnectionState>,
)  {

    @Stable
    data class Rhasspy2HermesConnectionConfigurationData internal constructor(
        val host: String,
        val timeout: Long?,
        val bearerToken: String,
        val isSSLVerificationDisabled: Boolean,
    ) : IConfigurationData {

        val timeoutText: String = timeout.toStringOrEmpty()

    }

}