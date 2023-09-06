package org.rhasspy.mobile.viewmodel.configuration.connections.http

sealed interface HttpConnectionConfigurationUiEvent {

    sealed interface Change : HttpConnectionConfigurationUiEvent {

        data class UpdateHttpClientServerEndpointHost(val host: String) : Change
        data class UpdateHttpClientTimeout(val text: String) : Change
        data class SetHttpSSLVerificationDisabled(val disabled: Boolean) : Change

    }

    sealed interface Action : HttpConnectionConfigurationUiEvent {

        data object BackClick : Action

    }

}