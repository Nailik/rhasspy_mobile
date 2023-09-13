package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import org.rhasspy.mobile.data.service.option.SpeechToTextOption

sealed interface SpeechToTextConfigurationUiEvent {

    sealed interface Change : SpeechToTextConfigurationUiEvent {

        data class SelectSpeechToTextOption(val option: SpeechToTextOption) : Change
        data class SetUseSpeechToTextMqttSilenceDetection(val enabled: Boolean) : Change

    }

    sealed interface Action : SpeechToTextConfigurationUiEvent {

        data object BackClick : Action

    }

}