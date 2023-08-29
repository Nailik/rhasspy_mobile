package org.rhasspy.mobile.logic.domains.voiceactivitydetection

import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption

data class VoiceActivityDetectionParams(
    val voiceActivityDetectionOption: VoiceActivityDetectionOption,
    val automaticSilenceDetectionAudioLevel: Float,
    val automaticSilenceDetectionTime: Long?,
    val automaticSilenceDetectionMinimumTime: Long?
)