package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import kotlin.time.Duration

@Serializable
data class TtsDomainData(
    val option: TextToSpeechOption,
    val rhasspy2HermesMqttTimeout: Duration,
)