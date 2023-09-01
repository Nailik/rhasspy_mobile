package org.rhasspy.mobile.viewmodel.configuration.connections.http.detail

sealed interface HttpConnectionDetailConfigurationUiEvent {

    sealed interface Change : HttpConnectionDetailConfigurationUiEvent {

        data class UpdateHttpClientServerEndpointHost(val host: String) : Change
        data class UpdateHttpClientTimeout(val text: String) : Change
        data class UpdateHttpClientServerEndpointPort(val port: String) : Change
        data class SetHttpSSLVerificationDisabled(val disabled: Boolean) : Change
        data class SetHermesEnabled(val enabled: Boolean) : Change
        data class SetWyomingEnabled(val enabled: Boolean) : Change
        data class SetHomeAssistantEnabled(val enabled: Boolean) : Change

    }

    sealed interface Action : HttpConnectionDetailConfigurationUiEvent {

        data object BackClick : Action

    }

}