package org.rhasspy.mobile.logic.services.homeassistant

import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption

data class HomeAssistantServiceParams(
    val siteId: String = ConfigurationSetting.siteId.value,
    val intentHandlingHomeAssistantOption: HomeAssistantIntentHandlingOption = ConfigurationSetting.intentHandlingHomeAssistantOption.value
)