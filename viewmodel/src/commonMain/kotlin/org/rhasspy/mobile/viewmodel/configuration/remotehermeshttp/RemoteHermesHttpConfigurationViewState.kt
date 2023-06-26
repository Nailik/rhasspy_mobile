package org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.platformspecific.toStringOrEmpty
import org.rhasspy.mobile.settings.ConfigurationSetting

@Stable
data class RemoteHermesHttpConfigurationViewState internal constructor(
    val editData: RemoteHermesHttpConfigurationData
) {

    @Stable
    data class RemoteHermesHttpConfigurationData internal constructor(
        val httpClientServerEndpointHost: String = ConfigurationSetting.httpClientServerEndpointHost.value,
        val httpClientServerEndpointPort: Int? = ConfigurationSetting.httpClientServerEndpointPort.value,
        val httpClientTimeout: Long? = ConfigurationSetting.httpClientTimeout.value,
        val isHttpSSLVerificationDisabled: Boolean = ConfigurationSetting.isHttpClientSSLVerificationDisabled.value
    ) {

        val httpClientServerEndpointPortText: String = httpClientServerEndpointPort.toStringOrEmpty()
        val httpClientTimeoutText: String = httpClientTimeout.toStringOrEmpty()

    }

}