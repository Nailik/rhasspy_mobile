package org.rhasspy.mobile.viewmodel.settings.silencedetection

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.naNToZero
import org.rhasspy.mobile.platformspecific.toStringOrEmpty
import org.rhasspy.mobile.settings.AppSetting
import kotlin.math.log

class SilenceDetectionSettingsViewStateCreator(
    private val audioRecorder: IAudioRecorder
) {

    operator fun invoke(): StateFlow<SilenceDetectionSettingsViewState> {
        val viewState = combineStateFlow(
            audioRecorder.maxVolume,
            audioRecorder.isRecording,
            AppSetting.isAutomaticSilenceDetectionEnabled.data,
            AppSetting.automaticSilenceDetectionAudioLevel.data,
            AppSetting.automaticSilenceDetectionMinimumTime.data,
            AppSetting.automaticSilenceDetectionTime.data
        ).mapReadonlyState {
            getViewState()
        }

        return viewState
    }

    private fun getViewState(): SilenceDetectionSettingsViewState {
        return SilenceDetectionSettingsViewState(
            silenceDetectionTimeText = AppSetting.automaticSilenceDetectionTime.value.toStringOrEmpty(),
            silenceDetectionMinimumTimeText = AppSetting.automaticSilenceDetectionMinimumTime.value.toStringOrEmpty(),
            isSilenceDetectionEnabled = AppSetting.isAutomaticSilenceDetectionEnabled.value,
            silenceDetectionAudioLevel = AppSetting.automaticSilenceDetectionAudioLevel.value,
            silenceDetectionAudioLevelPercentage = log(
                AppSetting.automaticSilenceDetectionAudioLevel.value,
                audioRecorder.absoluteMaxVolume
            ).naNToZero(),
            currentVolume = audioRecorder.maxVolume.value.toString(),
            audioLevelPercentage = log(
                audioRecorder.maxVolume.value,
                audioRecorder.absoluteMaxVolume
            ).naNToZero(),
            isAudioLevelBiggerThanMax = audioRecorder.maxVolume.value > AppSetting.automaticSilenceDetectionAudioLevel.value,
            isRecording = audioRecorder.isRecording.value
        )
    }

}