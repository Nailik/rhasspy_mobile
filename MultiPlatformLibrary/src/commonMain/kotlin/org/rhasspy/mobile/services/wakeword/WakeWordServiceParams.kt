package org.rhasspy.mobile.services.wakeword

import org.rhasspy.mobile.data.PorcupineLanguageOptions
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.settings.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.settings.porcupine.PorcupineDefaultKeyword

class WakeWordServiceParams(
    val wakeWordOption: WakeWordOption = ConfigurationSettings.wakeWordOption.value,
    val wakeWordPorcupineAccessToken: String = ConfigurationSettings.wakeWordPorcupineAccessToken.value,
    val wakeWordPorcupineKeywordDefaultOptions: Set<PorcupineDefaultKeyword> = ConfigurationSettings.wakeWordPorcupineKeywordDefaultOptions.value,
    val wakeWordPorcupineKeywordCustomOptions: Set<PorcupineCustomKeyword> = ConfigurationSettings.wakeWordPorcupineKeywordCustomOptions.value,
    val wakeWordPorcupineLanguage: PorcupineLanguageOptions = ConfigurationSettings.wakeWordPorcupineLanguage.value
)