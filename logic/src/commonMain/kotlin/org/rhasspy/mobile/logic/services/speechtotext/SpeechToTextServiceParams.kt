package org.rhasspy.mobile.logic.services.speechtotext

import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting

data class SpeechToTextServiceParams(
    val speechToTextOption: SpeechToTextOption = ConfigurationSetting.speechToTextOption.value
)