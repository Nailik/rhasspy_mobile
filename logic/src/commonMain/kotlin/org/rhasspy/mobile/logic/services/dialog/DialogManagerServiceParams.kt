package org.rhasspy.mobile.logic.services.dialog

import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.data.service.option.WakeWordOption

data class DialogManagerServiceParams(
    val option: DialogManagementOption = ConfigurationSetting.dialogManagementOption.value,
    val wakeWordOption: WakeWordOption = ConfigurationSetting.wakeWordOption.value,
    val speechToTextOption: SpeechToTextOption = ConfigurationSetting.speechToTextOption.value,
    val intentRecognitionOption: IntentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value,
    val asrTimeout: Long = ConfigurationSetting.textAsrTimeout.value,
    val intentRecognitionTimeout: Long = ConfigurationSetting.intentRecognitionTimeout.value,
    val recordingTimeout: Long = ConfigurationSetting.recordingTimeout.value,
)