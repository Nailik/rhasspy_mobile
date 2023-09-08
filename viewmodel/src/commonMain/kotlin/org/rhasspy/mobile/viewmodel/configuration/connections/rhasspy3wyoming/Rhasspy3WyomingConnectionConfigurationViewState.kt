package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.data.toStringOrEmpty
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class Rhasspy3WyomingConnectionConfigurationViewState internal constructor(
    override val editData: Rhasspy3WyomingConnectionConfigurationData
) : IConfigurationViewState {

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