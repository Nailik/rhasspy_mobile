package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.IntentDomainOption
import kotlin.time.Duration

@Serializable
data class IntentDomainData(
    val option: IntentDomainOption,
    val isRhasspy2HermesHttpHandleWithRecognition: Boolean,
    val rhasspy2HermesHttpIntentHandlingTimeout: Duration,
    val timeout: Duration,
)