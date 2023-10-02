package org.rhasspy.mobile.viewmodel.configuration.mic

sealed interface MicDomainConfigurationUiEvent {

    sealed interface Action : MicDomainConfigurationUiEvent {

        data object OpenInputFormatConfigurationScreen : Action
        data object OpenOutputFormatConfigurationScreen : Action
        data object RequestMicrophonePermission : Action

    }

    sealed interface Change : MicDomainConfigurationUiEvent {

        data class SetUsePauseOnMediaPlayback(val value: Boolean) : Change

    }

}