package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.data.toStringOrEmpty
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState.IConfigurationData

@Stable
data class Rhasspy3WyomingConnectionConfigurationViewState internal constructor(
    val editData: Rhasspy3WyomingConnectionConfigurationData,
    val connectionState: StateFlow<ConnectionState>,
) {

    @Stable
    data class Rhasspy3WyomingConnectionConfigurationData internal constructor(
        val host: String,
        val timeout: Long?,
        val bearerToken: String,
        val isSSLVerificationDisabled: Boolean,
    ) : IConfigurationData {

        val timeoutText: String = timeout.toStringOrEmpty()

    }

}