package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import org.rhasspy.mobile.data.service.option.SpeechToTextOption

sealed interface SpeechToTextConfigurationUiEvent {

    sealed interface Change : SpeechToTextConfigurationUiEvent {
        data class SelectSpeechToTextOption(val option: SpeechToTextOption) : Change
        data class SetUseCustomHttpEndpoint(val enabled: Boolean) : Change
        data class SetUseSpeechToTextMqttSilenceDetection(val enabled: Boolean) : Change
        data class UpdateSpeechToTextHttpEndpoint(val endpoint: String) : Change
    }

    sealed interface Action : SpeechToTextConfigurationUiEvent {

        object TestSpeechToTextToggleRecording: Action

    }

}