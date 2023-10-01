package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes

sealed interface Rhasspy2HermesConnectionConfigurationUiEvent {

    sealed interface Change : Rhasspy2HermesConnectionConfigurationUiEvent {

        data class UpdateRhasspy2HermesServerEndpointHost(val host: String) : Change
        data class UpdateRhasspy2HermesTimeout(val text: String) : Change
        data class UpdateRhasspy2HermesAccessToken(val text: String) : Change
        data class SetRhasspy2HermesSSLVerificationDisabled(val disabled: Boolean) : Change

    }

    sealed interface Action : Rhasspy2HermesConnectionConfigurationUiEvent {

        data object AccessTokenQRCodeClick : Action

    }

}