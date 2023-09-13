package org.rhasspy.mobile.logic.domains.wakeword

import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.domain.AudioInputDomainData
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption

internal data class WakeWordServiceParams(
    val isMicrophonePermissionEnabled: Boolean,
    val isEnabled: Boolean,
    val isAutoPauseOnMediaPlayback: Boolean,
    val audioInputDomainData: AudioInputDomainData,
    val wakeWordOption: WakeWordOption,
    val wakeWordPorcupineAccessToken: String,
    val wakeWordPorcupineKeywordDefaultOptions: ImmutableList<PorcupineDefaultKeyword>,
    val wakeWordPorcupineKeywordCustomOptions: ImmutableList<PorcupineCustomKeyword>,
    val wakeWordPorcupineLanguage: PorcupineLanguageOption,
    val wakeWordUdpOutputHost: String,
    val wakeWordUdpOutputPort: Int
)