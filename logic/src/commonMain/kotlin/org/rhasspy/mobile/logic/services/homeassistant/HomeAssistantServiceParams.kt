package org.rhasspy.mobile.logic.services.homeassistant

import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption

internal data class HomeAssistantServiceParams(
    val siteId: String,
    val intentHandlingHomeAssistantOption: HomeAssistantIntentHandlingOption
)