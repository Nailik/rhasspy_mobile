package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption
import kotlin.time.Duration

@Serializable
data class VadDomainData(
    val option: VoiceActivityDetectionOption,
    val timeout: Duration,
    val automaticSilenceDetectionAudioLevel: Float,
    val automaticSilenceDetectionTime: Duration,
    val automaticSilenceDetectionMinimumTime: Duration,
)