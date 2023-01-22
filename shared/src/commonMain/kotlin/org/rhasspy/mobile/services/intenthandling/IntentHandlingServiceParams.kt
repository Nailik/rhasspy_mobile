package org.rhasspy.mobile.services.intenthandling

import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.IntentHandlingOption

data class IntentHandlingServiceParams(
    val intentHandlingOption: IntentHandlingOption = ConfigurationSetting.intentHandlingOption.value
)