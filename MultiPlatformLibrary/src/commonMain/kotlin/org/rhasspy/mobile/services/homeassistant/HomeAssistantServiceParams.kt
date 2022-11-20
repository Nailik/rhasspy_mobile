package org.rhasspy.mobile.services.homeassistant

import org.rhasspy.mobile.data.HomeAssistantIntentHandlingOptions
import org.rhasspy.mobile.settings.ConfigurationSettings

data class HomeAssistantServiceParams(
    val siteId: String = ConfigurationSettings.siteId.value,
    val intentHandlingHomeAssistantOption: HomeAssistantIntentHandlingOptions = ConfigurationSettings.intentHandlingHomeAssistantOption.value
)