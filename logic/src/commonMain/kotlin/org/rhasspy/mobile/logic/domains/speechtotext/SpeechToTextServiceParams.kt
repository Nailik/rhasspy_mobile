package org.rhasspy.mobile.logic.domains.speechtotext

import org.rhasspy.mobile.data.domain.AudioInputDomainData
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption

internal data class SpeechToTextServiceParams(
    val isAutoPauseOnMediaPlayback: Boolean,
    val speechToTextOption: SpeechToTextOption,
    val dialogManagementOption: DialogManagementOption,
    val audioInputDomainData: AudioInputDomainData,
    val httpConnectionId: Long?
)