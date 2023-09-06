package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.connection.HttpConnectionParams
import org.rhasspy.mobile.platformspecific.toStringOrEmpty
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class Rhasspy3WyomingConnectionConfigurationViewState internal constructor(
    override val editData: HttpConfigurationData
) : IConfigurationViewState {

    @Stable
    data class HttpConfigurationData internal constructor(
        val id: Long?,
        val host: String,
        val timeout: Long?,
        val bearerToken: String?,
        val isSSLVerificationDisabled: Boolean,
    ) : IConfigurationData {

        internal constructor(connection: HttpConnectionParams = ConfigurationSetting.httpConnection.value) : this(
            id = connection.id,
            host = connection.host,
            timeout = connection.timeout,
            bearerToken = connection.bearerToken,
            isSSLVerificationDisabled = connection.isSSLVerificationDisabled,
        )

        val timeoutText: String = timeout.toStringOrEmpty()

    }

}