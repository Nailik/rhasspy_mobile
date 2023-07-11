package org.rhasspy.mobile.logic.services.dialog

import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.data.service.option.WakeWordOption

internal data class DialogManagerServiceParams(
    val option: DialogManagementOption,
    val wakeWordOption: WakeWordOption,
    val speechToTextOption: SpeechToTextOption,
    val intentRecognitionOption: IntentRecognitionOption,
    val asrTimeout: Long,
    val intentRecognitionTimeout: Long,
    val recordingTimeout: Long
)