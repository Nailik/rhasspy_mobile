package org.rhasspy.mobile.services.homeassistant

import org.rhasspy.mobile.settings.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.settings.ConfigurationSetting

data class HomeAssistantServiceParams(
    val siteId: String = ConfigurationSetting.siteId.value,
    val intentHandlingHomeAssistantOption: HomeAssistantIntentHandlingOption = ConfigurationSetting.intentHandlingHomeAssistantOption.value
)