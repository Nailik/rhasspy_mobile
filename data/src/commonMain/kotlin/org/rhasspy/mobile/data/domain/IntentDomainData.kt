package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.IntentDomainOption
import kotlin.time.Duration

@Serializable
data class IntentDomainData(
    val option: IntentDomainOption,
    val isRhasspy2HermesHttpHandleWithRecognition: Boolean,
    val rhasspy2HermesHttpHandleTimeout: Duration,
    val rhasspy2HermesMqttHandleTimeout: Duration,
)