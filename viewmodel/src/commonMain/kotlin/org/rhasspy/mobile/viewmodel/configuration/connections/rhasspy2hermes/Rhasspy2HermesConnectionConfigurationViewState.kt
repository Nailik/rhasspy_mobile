package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.data.toStringOrEmpty
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class Rhasspy2HermesConnectionConfigurationViewState internal constructor(
    override val editData: HttpConnectionConfigurationData
) : IConfigurationViewState {

    @Stable
    data class HttpConnectionConfigurationData internal constructor(
        val host: String,
        val timeout: Long?,
        val bearerToken: String,
        val isSSLVerificationDisabled: Boolean,
    ) : IConfigurationData {

        val timeoutText: String = timeout.toStringOrEmpty()

    }

}