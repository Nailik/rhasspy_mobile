package org.rhasspy.mobile.logic.services.wakeword

import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.settings.option.PorcupineLanguageOption
import org.rhasspy.mobile.logic.settings.option.WakeWordOption
import org.rhasspy.mobile.logic.settings.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.logic.settings.porcupine.PorcupineDefaultKeyword

class WakeWordServiceParams(
    val wakeWordOption: WakeWordOption = ConfigurationSetting.wakeWordOption.value,
    val wakeWordPorcupineAccessToken: String = ConfigurationSetting.wakeWordPorcupineAccessToken.value,
    val wakeWordPorcupineKeywordDefaultOptions: Set<PorcupineDefaultKeyword> = ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value,
    val wakeWordPorcupineKeywordCustomOptions: Set<PorcupineCustomKeyword> = ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value,
    val wakeWordPorcupineLanguage: PorcupineLanguageOption = ConfigurationSetting.wakeWordPorcupineLanguage.value,
    val wakeWordUdpOutputHost: String = ConfigurationSetting.wakeWordUdpOutputHost.value,
    val wakeWordUdpOutputPort: Int = ConfigurationSetting.wakeWordUdpOutputPort.value
)