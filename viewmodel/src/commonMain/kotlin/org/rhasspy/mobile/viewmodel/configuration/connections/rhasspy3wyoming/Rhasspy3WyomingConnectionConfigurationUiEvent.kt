package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming

sealed interface Rhasspy3WyomingConnectionConfigurationUiEvent {

    sealed interface Change : Rhasspy3WyomingConnectionConfigurationUiEvent {

        data class UpdateRhasspy3WyomingServerEndpointHost(val host: String) : Change
        data class UpdateRhasspy3WyomingTimeout(val text: String) : Change
        data class SetRhasspy3WyomingSSLVerificationDisabled(val disabled: Boolean) : Change

    }

    sealed interface Action : Rhasspy3WyomingConnectionConfigurationUiEvent {

        data object BackClick : Action

    }

}