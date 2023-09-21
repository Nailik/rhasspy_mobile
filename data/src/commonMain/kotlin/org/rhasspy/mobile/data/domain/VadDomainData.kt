package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption

@Serializable
data class VadDomainData(
    val option: VoiceActivityDetectionOption,
    val automaticSilenceDetectionAudioLevel: Float,
    val automaticSilenceDetectionTime: Long?,
    val automaticSilenceDetectionMinimumTime: Long?
)