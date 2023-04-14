package org.rhasspy.mobile.logic.services.intentrecognition

import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting

data class IntentRecognitionServiceParams(
    val intentRecognitionOption: IntentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value
)