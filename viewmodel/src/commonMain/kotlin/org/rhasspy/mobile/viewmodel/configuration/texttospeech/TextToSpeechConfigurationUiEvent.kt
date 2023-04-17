package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import org.rhasspy.mobile.data.service.option.TextToSpeechOption

sealed interface TextToSpeechConfigurationUiEvent {

    sealed interface Change : TextToSpeechConfigurationUiEvent {
        data class SelectTextToSpeechOption(val option: TextToSpeechOption) : Change
        data class SetUseCustomHttpEndpoint(val enabled: Boolean) : Change
        data class UpdateTextToSpeechHttpEndpoint(val endpoint: String) : Change
        data class UpdateTestTextToSpeechText(val text: String) : Change
    }

    sealed interface Action: TextToSpeechConfigurationUiEvent {

        object TestRemoteHermesHttpTextToSpeechTest: Action

    }

}