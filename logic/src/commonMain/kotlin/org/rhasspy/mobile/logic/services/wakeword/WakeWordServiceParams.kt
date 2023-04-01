package org.rhasspy.mobile.logic.services.wakeword

import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption

class WakeWordServiceParams(
    val wakeWordOption: WakeWordOption = ConfigurationSetting.wakeWordOption.value,
    val wakeWordPorcupineAccessToken: String = ConfigurationSetting.wakeWordPorcupineAccessToken.value,
    val wakeWordPorcupineKeywordDefaultOptions: Set<PorcupineDefaultKeyword> = ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value,
    val wakeWordPorcupineKeywordCustomOptions: Set<PorcupineCustomKeyword> = ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value,
    val wakeWordPorcupineLanguage: PorcupineLanguageOption = ConfigurationSetting.wakeWordPorcupineLanguage.value,
    val wakeWordUdpOutputHost: String = ConfigurationSetting.wakeWordUdpOutputHost.value,
    val wakeWordUdpOutputPort: Int = ConfigurationSetting.wakeWordUdpOutputPort.value
)