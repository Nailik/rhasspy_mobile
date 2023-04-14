package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import org.rhasspy.mobile.data.service.option.TextToSpeechOption

sealed interface TextToSpeechConfigurationUiAction {

    sealed interface Change : TextToSpeechConfigurationUiAction {
        data class SelectTextToSpeechOption(val option: TextToSpeechOption) : Change
        object ToggleUseCustomHttpEndpoint : Change
        data class UpdateTextToSpeechHttpEndpoint(val value: String) : Change
    }

}