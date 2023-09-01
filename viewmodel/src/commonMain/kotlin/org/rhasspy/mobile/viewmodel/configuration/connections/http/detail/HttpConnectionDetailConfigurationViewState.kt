package org.rhasspy.mobile.viewmodel.configuration.connections.http.detail

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.connection.HttpConnection
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
        val port: Int?,
        val timeout: Long?,
        val bearerToken: String?,
        val isSSLVerificationDisabled: Boolean,
        val isHermes: Boolean = false,
        val isWyoming: Boolean = false,
        val isHomeAssistant: Boolean = false,
    ) : IConfigurationData {

        internal constructor(connection: HttpConnection?) : this(
            host = connection?.host ?: "",
            port = connection?.port,
            timeout = connection?.timeout,
            bearerToken = connection?.bearerToken,
            isSSLVerificationDisabled = connection?.isSSLVerificationDisabled ?: false,
            isHermes = connection?.isHermes ?: false,
            isWyoming = connection?.isWyoming ?: false,
            isHomeAssistant = connection?.isHomeAssistant ?: false
        )

        val portText: String = port.toStringOrEmpty()
        val timeoutText: String = timeout.toStringOrEmpty()

    }

}