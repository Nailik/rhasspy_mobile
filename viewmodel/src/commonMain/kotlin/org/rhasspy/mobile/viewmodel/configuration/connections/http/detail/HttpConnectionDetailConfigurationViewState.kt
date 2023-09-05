package org.rhasspy.mobile.viewmodel.configuration.connections.http.detail

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.connection.HttpConnectionParams
import org.rhasspy.mobile.platformspecific.toStringOrEmpty
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class HttpConnectionDetailConfigurationViewState internal constructor(
    override val editData: HttpConfigurationData
) : IConfigurationViewState {

    @Stable
    data class HttpConfigurationData internal constructor(
        val host: String,
        val timeout: Long?,
        val bearerToken: String?,
        val isSSLVerificationDisabled: Boolean,
    ) : IConfigurationData {

        internal constructor(connection: HttpConnectionParams?) : this(
            host = connection?.host ?: "",
            timeout = connection?.timeout,
            bearerToken = connection?.bearerToken,
            isSSLVerificationDisabled = connection?.isSSLVerificationDisabled ?: false,
        )

        val timeoutText: String = timeout.toStringOrEmpty()

    }

}