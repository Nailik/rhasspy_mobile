package org.rhasspy.mobile.viewmodel.settings.silencedetection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorder
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.AppSetting
import kotlin.math.log

class SilenceDetectionSettingsViewStateCreator(
    private val audioRecorder: AudioRecorder
) {
    private val updaterScope = CoroutineScope(Dispatchers.IO)

    operator fun invoke(): StateFlow<SilenceDetectionSettingsViewState> {
        val viewState = MutableStateFlow(getViewState())

        updaterScope.launch {
            combineStateFlow(
                audioRecorder.maxVolume,
                audioRecorder.isRecording,
                AppSetting.isAutomaticSilenceDetectionEnabled.data,
                AppSetting.automaticSilenceDetectionAudioLevel.data,
                AppSetting.automaticSilenceDetectionMinimumTime.data,
                AppSetting.automaticSilenceDetectionTime.data
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState(): SilenceDetectionSettingsViewState {
        return SilenceDetectionSettingsViewState(
            silenceDetectionTimeText = AppSetting.automaticSilenceDetectionTime.value.toString(),
            silenceDetectionMinimumTimeText = AppSetting.automaticSilenceDetectionMinimumTime.value.toString(),
            isSilenceDetectionEnabled = AppSetting.isAutomaticSilenceDetectionEnabled.value,
            silenceDetectionAudioLevel = AppSetting.automaticSilenceDetectionAudioLevel.value,
            silenceDetectionAudioLevelPercentage = (log(AppSetting.automaticSilenceDetectionAudioLevel.value, audioRecorder.absoluteMaxVolume)),
            currentVolume = audioRecorder.maxVolume.value.toString(),
            audioLevelPercentage = (log(audioRecorder.maxVolume.value, audioRecorder.absoluteMaxVolume)),
            isAudioLevelBiggerThanMax = audioRecorder.maxVolume.value > AppSetting.automaticSilenceDetectionAudioLevel.value,
            isRecording = audioRecorder.isRecording.value
        )
    }

}