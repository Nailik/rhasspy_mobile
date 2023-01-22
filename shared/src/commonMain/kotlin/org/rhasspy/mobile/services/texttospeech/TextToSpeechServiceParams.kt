package org.rhasspy.mobile.services.texttospeech

import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.TextToSpeechOption

data class TextToSpeechServiceParams(
    val textToSpeechOption: TextToSpeechOption = ConfigurationSetting.textToSpeechOption.value
)