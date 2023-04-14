package org.rhasspy.mobile.logic.services.intenthandling

import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting

data class IntentHandlingServiceParams(
    val intentHandlingOption: IntentHandlingOption = ConfigurationSetting.intentHandlingOption.value
)