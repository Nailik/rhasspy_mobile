package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption

@Serializable
data class WakeDomainData(
    val wakeWordOption: WakeWordOption,
    val wakeWordPorcupineAccessToken: String,
    val wakeWordPorcupineKeywordDefaultOptions: List<PorcupineDefaultKeyword>,
    val wakeWordPorcupineKeywordCustomOptions: List<PorcupineCustomKeyword>,
    val wakeWordPorcupineLanguage: PorcupineLanguageOption,
    val wakeWordUdpOutputHost: String,
    val wakeWordUdpOutputPort: Int
)