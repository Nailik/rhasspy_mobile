package org.rhasspy.mobile.logic.services.speechtotext

import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption

internal data class SpeechToTextServiceParams(
    val speechToTextOption: SpeechToTextOption,
    val dialogManagementOption: DialogManagementOption,
)