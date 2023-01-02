package org.rhasspy.mobile.services.homeassistant

import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.HomeAssistantIntentHandlingOption

data class HomeAssistantServiceParams(
    val siteId: String = ConfigurationSetting.siteId.value,
    val intentHandlingHomeAssistantOption: HomeAssistantIntentHandlingOption = ConfigurationSetting.intentHandlingHomeAssistantOption.value
)