package org.rhasspy.mobile.logic.services.speechtotext

import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.data.serviceoption.SpeechToTextOption

data class SpeechToTextServiceParams(
    val speechToTextOption: SpeechToTextOption = ConfigurationSetting.speechToTextOption.value
)