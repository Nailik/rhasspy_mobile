package org.rhasspy.mobile.viewmodel.configuration.vad

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.naNToZero
import kotlin.math.log

class AudioRecorderViewStateCreator(
    private val audioRecorder: IAudioRecorder
) {

    operator fun invoke(voiceActivityDetectionConfigurationData: StateFlow<VadDomainViewState>): StateFlow<AudioRecorderViewState> {
        return combineStateFlow(
            audioRecorder.maxVolume,
            audioRecorder.isRecording,
            voiceActivityDetectionConfigurationData,
        ).mapReadonlyState {
            getViewState(voiceActivityDetectionConfigurationData)
        }
    }

    private fun getViewState(
        voiceActivityDetectionConfigurationData: StateFlow<VadDomainViewState>
    ): AudioRecorderViewState {
        return AudioRecorderViewState(
            currentVolume = audioRecorder.maxVolume.value.toString(),
            audioLevelPercentage = log(audioRecorder.maxVolume.value, audioRecorder.absoluteMaxVolume).naNToZero(),
            isAudioLevelBiggerThanMax = audioRecorder.maxVolume.value > voiceActivityDetectionConfigurationData.value.editData.localSilenceDetectionSetting.silenceDetectionAudioLevel,
            isRecording = audioRecorder.isRecording.value,
            silenceDetectionAudioLevelPercentage = log(
                voiceActivityDetectionConfigurationData.value.editData.localSilenceDetectionSetting.silenceDetectionAudioLevel,
                audioRecorder.absoluteMaxVolume
            ).naNToZero(),
        )
    }

}