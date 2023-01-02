package org.rhasspy.mobile.services.rhasspyactions

import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.*

data class RhasspyActionsServiceParams(
    val intentRecognitionOption: IntentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value,
    val textToSpeechOption: TextToSpeechOption = ConfigurationSetting.textToSpeechOption.value,
    val audioPlayingOption: AudioPlayingOption = ConfigurationSetting.audioPlayingOption.value,
    val speechToTextOption: SpeechToTextOption = ConfigurationSetting.speechToTextOption.value,
    val intentHandlingOption: IntentHandlingOption = ConfigurationSetting.intentHandlingOption.value
)