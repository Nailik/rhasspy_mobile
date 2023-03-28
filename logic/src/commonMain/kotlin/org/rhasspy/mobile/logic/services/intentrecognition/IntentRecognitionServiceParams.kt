package org.rhasspy.mobile.logic.services.intentrecognition

import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.data.serviceoption.IntentRecognitionOption

data class IntentRecognitionServiceParams(
    val intentRecognitionOption: IntentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value
)