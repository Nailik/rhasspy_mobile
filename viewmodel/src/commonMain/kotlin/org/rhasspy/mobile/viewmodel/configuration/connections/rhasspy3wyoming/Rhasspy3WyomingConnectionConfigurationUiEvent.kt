package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming

sealed interface Rhasspy3WyomingConnectionConfigurationUiEvent {

    sealed interface Change : Rhasspy3WyomingConnectionConfigurationUiEvent {

        data class UpdateHomeAssistantClientServerEndpointHost(val host: String) : Change
        data class UpdateHomeAssistantClientTimeout(val text: String) : Change
        data class SetHomeAssistantSSLVerificationDisabled(val disabled: Boolean) : Change

    }

    sealed interface Action : Rhasspy3WyomingConnectionConfigurationUiEvent {

        data object BackClick : Action

    }

}