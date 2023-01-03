package org.rhasspy.mobile.services.intentrecognition

import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.*

data class IntentRecognitionServiceParams(
    val intentRecognitionOption: IntentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value
)