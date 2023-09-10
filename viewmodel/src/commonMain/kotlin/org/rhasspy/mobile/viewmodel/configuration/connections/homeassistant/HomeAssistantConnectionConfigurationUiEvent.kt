package org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant

sealed interface HomeAssistantConnectionConfigurationUiEvent {

    sealed interface Change : HomeAssistantConnectionConfigurationUiEvent {

        data class UpdateHomeAssistantClientServerEndpointHost(val host: String) : Change
        data class UpdateHomeAssistantClientTimeout(val text: String) : Change
        data class UpdateHomeAssistantAccessToken(val text: String) : Change
        data class SetHomeAssistantSSLVerificationDisabled(val disabled: Boolean) : Change

    }

    sealed interface Action : HomeAssistantConnectionConfigurationUiEvent {

        data object BackClick : Action
        data object AccessTokenQRCodeClick : Action

    }

}