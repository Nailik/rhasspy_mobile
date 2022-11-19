package org.rhasspy.mobile.services.rhasspyactions

import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.settings.ConfigurationSettings

data class RhasspyActionsServiceParams(
    val intentRecognitionOption: IntentRecognitionOptions,
    val textToSpeechOption: TextToSpeechOptions,
    val audioPlayingOption: AudioPlayingOptions,
    val speechToTextOption: SpeechToTextOptions,
    val intentHandlingOption: IntentHandlingOptions
) {
    companion object {
        fun loadFromConfiguration(): RhasspyActionsServiceParams {
            return RhasspyActionsServiceParams(
                intentRecognitionOption = ConfigurationSettings.intentRecognitionOption.value,
                textToSpeechOption = ConfigurationSettings.textToSpeechOption.value,
                audioPlayingOption = ConfigurationSettings.audioPlayingOption.value,
                speechToTextOption = ConfigurationSettings.speechToTextOption.value,
                intentHandlingOption = ConfigurationSettings.intentHandlingOption.value
            )
        }
    }
}