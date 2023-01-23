package org.rhasspy.mobile.logic.services.dialog

import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.settings.option.DialogManagementOption
import org.rhasspy.mobile.logic.settings.option.IntentRecognitionOption
import org.rhasspy.mobile.logic.settings.option.SpeechToTextOption
import org.rhasspy.mobile.logic.settings.option.WakeWordOption

data class DialogManagerServiceParams(
    val option: DialogManagementOption = ConfigurationSetting.dialogManagementOption.value,
    val wakeWordOption: WakeWordOption = ConfigurationSetting.wakeWordOption.value,
    val speechToTextOption: SpeechToTextOption = ConfigurationSetting.speechToTextOption.value,
    val intentRecognitionOption: IntentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value,
)