package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import org.rhasspy.mobile.data.service.option.SpeechToTextOption

sealed interface SpeechToTextConfigurationUiAction {

    sealed interface Change : SpeechToTextConfigurationUiAction {
        data class SelectSpeechToTextOption(val option: SpeechToTextOption): Change
        object ToggleUseCustomHttpEndpoint: Change
        object ToggleUseSpeechToTextMqttSilenceDetection: Change
        data class UpdateSpeechToTextHttpEndpoint(val value: String): Change
    }

}