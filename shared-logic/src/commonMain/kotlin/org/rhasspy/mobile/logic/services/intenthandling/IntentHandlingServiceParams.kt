package org.rhasspy.mobile.logic.services.intenthandling

import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.settings.option.IntentHandlingOption

data class IntentHandlingServiceParams(
    val intentHandlingOption: IntentHandlingOption = ConfigurationSetting.intentHandlingOption.value
)