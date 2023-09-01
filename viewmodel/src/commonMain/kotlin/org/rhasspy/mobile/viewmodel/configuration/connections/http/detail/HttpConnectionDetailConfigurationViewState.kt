package org.rhasspy.mobile.viewmodel.configuration.connections.http.detail

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.connection.HttpConnection
import org.rhasspy.mobile.platformspecific.toStringOrEmpty
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class HttpConnectionDetailConfigurationViewState internal constructor(
    override val editData: RemoteHermesHttpConfigurationData
) : IConfigurationViewState {

    @Stable
    data class RemoteHermesHttpConfigurationData internal constructor(
        val httpClientServerEndpointHost: String,
        val httpClientServerEndpointPort: Int?,
        val httpClientTimeout: Long?,
        val isSSLVerificationDisabled: Boolean,
        val isHermes: Boolean = false,
        val isWyoming: Boolean = false,
        val isHomeAssistant: Boolean = false,
    ) : IConfigurationData {

        internal constructor(connection: HttpConnection?) : this(
            httpClientServerEndpointHost = connection?.host ?: "",
            httpClientServerEndpointPort = connection?.port,
            httpClientTimeout = connection?.timeout,
            isSSLVerificationDisabled = connection?.isSslVerificationDisabled ?: false,
            isHermes = connection?.isHermes ?: false,
            isWyoming = connection?.isWyoming ?: false,
            isHomeAssistant = connection?.isHomeAssistant ?: false
        )

        val httpClientServerEndpointPortText: String = httpClientServerEndpointPort.toStringOrEmpty()
        val httpClientTimeoutText: String = httpClientTimeout.toStringOrEmpty()

    }

}