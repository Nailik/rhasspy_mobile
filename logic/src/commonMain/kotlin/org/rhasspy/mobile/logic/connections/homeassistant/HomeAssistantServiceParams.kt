package org.rhasspy.mobile.logic.connections.homeassistant

import org.rhasspy.mobile.data.connection.HttpConnectionParams
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption

internal data class HomeAssistantServiceParams(
    val siteId: String,
    val intentHandlingHomeAssistantOption: HomeAssistantIntentHandlingOption,
    val httpConnectionParams: HttpConnectionParams
)