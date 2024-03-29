package org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection

import androidx.compose.runtime.Stable

@Stable
data class AudioRecorderViewState internal constructor(
    val currentVolume: String,
    val audioLevelPercentage: Float,
    val isAudioLevelBiggerThanMax: Boolean,
    val isRecording: Boolean,
    val silenceDetectionAudioLevelPercentage: Float
)