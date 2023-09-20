package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption

@Serializable
data class HandleDomainData(
    val option: IntentHandlingOption,
    val homeAssistantIntentHandlingOption: HomeAssistantIntentHandlingOption
)