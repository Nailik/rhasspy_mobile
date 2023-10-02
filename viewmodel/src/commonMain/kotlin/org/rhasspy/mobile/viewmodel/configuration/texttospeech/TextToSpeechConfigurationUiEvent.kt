package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import org.rhasspy.mobile.data.service.option.TtsDomainOption

sealed interface TextToSpeechConfigurationUiEvent {

    sealed interface Change : TextToSpeechConfigurationUiEvent {

        data class SelectTextToSpeechOption(val option: TtsDomainOption) : Change

    }

    sealed interface Action : TextToSpeechConfigurationUiEvent {

        data object BackClick : Action

    }

}