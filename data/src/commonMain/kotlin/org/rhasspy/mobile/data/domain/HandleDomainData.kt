package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.HandleDomainOption
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import kotlin.time.Duration

@Serializable
data class HandleDomainData(
    val option: HandleDomainOption,
    val homeAssistantIntentHandlingOption: HomeAssistantIntentHandlingOption,
    val homeAssistantEventTimeout: Duration,
)