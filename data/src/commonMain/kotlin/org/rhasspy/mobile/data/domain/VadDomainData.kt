package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.VadDomainOption
import kotlin.time.Duration

@Serializable
data class VadDomainData(
    val option: VadDomainOption,
    val automaticSilenceDetectionAudioLevel: Float,
    val automaticSilenceDetectionTime: Duration,
    val automaticSilenceDetectionMinimumTime: Duration,
)