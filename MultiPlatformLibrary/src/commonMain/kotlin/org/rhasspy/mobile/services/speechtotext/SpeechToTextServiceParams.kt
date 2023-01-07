package org.rhasspy.mobile.services.speechtotext

import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.SpeechToTextOption

data class SpeechToTextServiceParams(
    val speechToTextOption: SpeechToTextOption = ConfigurationSetting.speechToTextOption.value,
    val speechToTextUdpOutputHost: String = ConfigurationSetting.speechToTextUdpOutputHost.value,
    val speechToTextUdpOutputPort: Int = ConfigurationSetting.speechToTextUdpOutputPort.value
)