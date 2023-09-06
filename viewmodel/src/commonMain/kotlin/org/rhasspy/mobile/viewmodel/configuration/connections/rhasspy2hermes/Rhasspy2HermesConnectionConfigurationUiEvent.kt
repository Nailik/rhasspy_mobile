package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes

sealed interface Rhasspy2HermesConnectionConfigurationUiEvent {

    sealed interface Change : Rhasspy2HermesConnectionConfigurationUiEvent {

        data class UpdateHomeAssistantClientServerEndpointHost(val host: String) : Change
        data class UpdateHomeAssistantClientTimeout(val text: String) : Change
        data class SetHomeAssistantSSLVerificationDisabled(val disabled: Boolean) : Change

    }

    sealed interface Action : Rhasspy2HermesConnectionConfigurationUiEvent {

        data object BackClick : Action

    }

}