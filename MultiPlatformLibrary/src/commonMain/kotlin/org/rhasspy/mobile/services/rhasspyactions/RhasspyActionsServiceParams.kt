package org.rhasspy.mobile.services.rhasspyactions

import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.settings.ConfigurationSettings

data class RhasspyActionsServiceParams(
    val intentRecognitionOption: IntentRecognitionOptions = ConfigurationSettings.intentRecognitionOption.value,
    val textToSpeechOption: TextToSpeechOptions = ConfigurationSettings.textToSpeechOption.value,
    val audioPlayingOption: AudioPlayingOptions = ConfigurationSettings.audioPlayingOption.value,
    val speechToTextOption: SpeechToTextOptions = ConfigurationSettings.speechToTextOption.value,
    val intentHandlingOption: IntentHandlingOptions = ConfigurationSettings.intentHandlingOption.value
)