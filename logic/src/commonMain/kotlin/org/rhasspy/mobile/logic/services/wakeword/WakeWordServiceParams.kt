package org.rhasspy.mobile.logic.services.wakeword

import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting

class WakeWordServiceParams(
    val wakeWordOption: WakeWordOption = ConfigurationSetting.wakeWordOption.value,
    val wakeWordPorcupineAccessToken: String = ConfigurationSetting.wakeWordPorcupineAccessToken.value,
    val wakeWordPorcupineKeywordDefaultOptions: ImmutableList<PorcupineDefaultKeyword> = ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value,
    val wakeWordPorcupineKeywordCustomOptions: ImmutableList<PorcupineCustomKeyword> = ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value,
    val wakeWordPorcupineLanguage: PorcupineLanguageOption = ConfigurationSetting.wakeWordPorcupineLanguage.value,
    val wakeWordUdpOutputHost: String = ConfigurationSetting.wakeWordUdpOutputHost.value,
    val wakeWordUdpOutputPort: Int = ConfigurationSetting.wakeWordUdpOutputPort.value
)