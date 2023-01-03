package org.rhasspy.mobile.services.speechtotext

import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.*

data class SpeechToTextServiceParams(
    val speechToTextOption: SpeechToTextOption = ConfigurationSetting.speechToTextOption.value
)