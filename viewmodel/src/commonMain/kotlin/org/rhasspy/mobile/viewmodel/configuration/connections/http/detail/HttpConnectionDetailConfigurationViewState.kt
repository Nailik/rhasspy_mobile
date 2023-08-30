package org.rhasspy.mobile.viewmodel.configuration.connections.http.detail

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.platformspecific.toStringOrEmpty
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class HttpConnectionDetailConfigurationViewState internal constructor(
    override val editData: RemoteHermesHttpConfigurationData
) : IConfigurationViewState {

    @Stable
    data class RemoteHermesHttpConfigurationData internal constructor(
        val httpClientServerEndpointHost: String = ConfigurationSetting.httpClientServerEndpointHost.value,
        val httpClientServerEndpointPort: Int? = ConfigurationSetting.httpClientServerEndpointPort.value,
        val httpClientTimeout: Long? = ConfigurationSetting.httpClientTimeout.value,
        val isHttpSSLVerificationDisabled: Boolean = ConfigurationSetting.isHttpClientSSLVerificationDisabled.value
    ) : IConfigurationData {

        val httpClientServerEndpointPortText: String = httpClientServerEndpointPort.toStringOrEmpty()
        val httpClientTimeoutText: String = httpClientTimeout.toStringOrEmpty()

    }

}