package org.rhasspy.mobile.services.dialog

import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.DialogManagementOption
import org.rhasspy.mobile.settings.option.IntentRecognitionOption
import org.rhasspy.mobile.settings.option.SpeechToTextOption
import org.rhasspy.mobile.settings.option.WakeWordOption

data class DialogManagerServiceParams(
    val option: DialogManagementOption = ConfigurationSetting.dialogManagementOption.value,
    val wakeWordOption: WakeWordOption = ConfigurationSetting.wakeWordOption.value,
    val speechToTextOption: SpeechToTextOption = ConfigurationSetting.speechToTextOption.value,
    val intentRecognitionOption: IntentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value,
)