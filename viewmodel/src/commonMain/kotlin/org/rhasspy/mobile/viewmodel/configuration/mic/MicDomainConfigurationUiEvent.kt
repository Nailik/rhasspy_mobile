package org.rhasspy.mobile.viewmodel.configuration.mic

sealed interface MicDomainConfigurationUiEvent {

    sealed interface Action : MicDomainConfigurationUiEvent {

        data object OpenInputFormatConfigurationScreen : Action
        data object OpenOutputFormatConfigurationScreen : Action

    }

    sealed interface Change : MicDomainConfigurationUiEvent {

        data class SetUseLoudnessEnhancer(val value: Boolean) : Change

        data class UpdateGain(val value: String) : Change

        data class SetUsePauseOnMediaPlayback(val value: Boolean) : Change

    }

}