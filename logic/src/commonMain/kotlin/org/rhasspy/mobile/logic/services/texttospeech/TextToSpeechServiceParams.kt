package org.rhasspy.mobile.logic.services.texttospeech

import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting

data class TextToSpeechServiceParams(
    val textToSpeechOption: TextToSpeechOption = ConfigurationSetting.textToSpeechOption.value
)