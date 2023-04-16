package org.rhasspy.mobile.viewmodel.settings.silencedetection

import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorder
import kotlin.math.log

data class SilenceDetectionSettingsViewState(
    val silenceDetectionTimeText: String ,
    val silenceDetectionMinimumTimeText: String ,
    val isSilenceDetectionEnabled: Boolean ,
    val silenceDetectionAudioLevel: Float,
    val silenceDetectionAudioLevelPercentage: Float,
    val currentVolume: String,
    val audioLevelPercentage: Float ,
    val isAudioLevelBiggerThanMax: Boolean ,
    val isRecording: Boolean
) {

    companion object {
        fun getInitialViewState(audioRecorder: AudioRecorder): SilenceDetectionSettingsViewState {
            return SilenceDetectionSettingsViewState(
                silenceDetectionTimeText = AppSetting.automaticSilenceDetectionTime.value.toString(),
                silenceDetectionMinimumTimeText = AppSetting.automaticSilenceDetectionMinimumTime.value.toString(),
                isSilenceDetectionEnabled = AppSetting.isAutomaticSilenceDetectionEnabled.value,
                silenceDetectionAudioLevel = AppSetting.automaticSilenceDetectionAudioLevel.value,
                silenceDetectionAudioLevelPercentage = (log(AppSetting.automaticSilenceDetectionAudioLevel.value.toDouble(), audioRecorder.absoluteMaxVolume)).toFloat(),
                currentVolume = 0.toString(),
                audioLevelPercentage = 0f,
                isAudioLevelBiggerThanMax = false,
                isRecording = false
            )
        }
    }

}