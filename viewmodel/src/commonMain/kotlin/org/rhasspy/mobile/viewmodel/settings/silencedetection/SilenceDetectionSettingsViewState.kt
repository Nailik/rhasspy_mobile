package org.rhasspy.mobile.viewmodel.settings.silencedetection

import androidx.compose.runtime.Stable

@Stable
data class SilenceDetectionSettingsViewState(
    val silenceDetectionTimeText: String,
    val silenceDetectionMinimumTimeText: String,
    val isSilenceDetectionEnabled: Boolean,
    val silenceDetectionAudioLevel: Float,
    val silenceDetectionAudioLevelPercentage: Float,
    val currentVolume: String,
    val audioLevelPercentage: Float,
    val isAudioLevelBiggerThanMax: Boolean,
    val isRecording: Boolean
)