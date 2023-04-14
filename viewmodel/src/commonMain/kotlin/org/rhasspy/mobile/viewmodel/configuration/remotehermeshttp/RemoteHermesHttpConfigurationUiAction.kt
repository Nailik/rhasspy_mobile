package org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp

sealed interface RemoteHermesHttpConfigurationUiAction {

    sealed interface Change : RemoteHermesHttpConfigurationUiAction {
        data class UpdateHttpClientServerEndpointHost(val value: String) : Change
        data class UpdateHttpClientTimeout(val value: String) : Change
        data class UpdateHttpClientServerEndpointPort(val value: String) : Change
        data class SetHttpSSLVerificationDisabled(val disabled: Boolean) : Change
    }

}