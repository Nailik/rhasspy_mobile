package org.rhasspy.mobile.logic.connections.homeassistant

import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption

internal data class HomeAssistantServiceParams(
    val siteId: String,
    val intentHandlingHomeAssistantOption: HomeAssistantIntentHandlingOption,
    val httpConnectionId: Long?
)