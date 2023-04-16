package org.rhasspy.mobile.viewmodel.settings.silencedetection

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorder
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Action.ToggleAudioLevelTest
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Change.*
import kotlin.math.log
import kotlin.math.pow

class SilenceDetectionSettingsViewModel(
    private val nativeApplication: NativeApplication
): ViewModel(), KoinComponent {

    private val audioRecorder: AudioRecorder = AudioRecorder()

    private val _viewState =
        MutableStateFlow(SilenceDetectionSettingsViewState.getInitialViewState(audioRecorder))
    val viewState = _viewState.readOnly

    fun onEvent(event: SilenceDetectionSettingsUiEvent) {
        when(event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            when(change) {
                is SetSilenceDetectionEnabled -> {
                    AppSetting.isAutomaticSilenceDetectionEnabled.value = change.enabled
                    it.copy(isSilenceDetectionEnabled = change.enabled)
                }
                is UpdateSilenceDetectionAudioLevelPercentage -> {
                    AppSetting.automaticSilenceDetectionAudioLevel.value = audioRecorder.absoluteMaxVolume.pow(change.percentage.toDouble()).toFloat()
                    it.copy(
                        silenceDetectionAudioLevel = audioRecorder.absoluteMaxVolume.pow(change.percentage.toDouble()).toFloat(),
                        silenceDetectionAudioLevelPercentage = change.percentage
                    )
                }
                is UpdateSilenceDetectionMinimumTime -> {
                    AppSetting.automaticSilenceDetectionMinimumTime.value = change.time.toIntOrZero()
                    it.copy(silenceDetectionMinimumTimeText = change.time)
                }
                is UpdateSilenceDetectionTime -> {
                    AppSetting.automaticSilenceDetectionTime.value = change.time.toIntOrZero()
                    it.copy(silenceDetectionTimeText = change.time)
                }
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            ToggleAudioLevelTest -> {
                if (audioRecorder.isRecording.value) {
                    audioRecorder.stopRecording()
                } else {
                    audioRecorder.startRecording()
                }
            }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.Default) {
            combineStateFlow(
                audioRecorder.maxVolume,
                audioRecorder.isRecording
            ){arr -> arr}
                .collect { data ->
                    _viewState.update {
                        it.copy(
                            audioLevelPercentage = (log((data[0] as Short).toDouble(), audioRecorder.absoluteMaxVolume)).toFloat(),
                            isAudioLevelBiggerThanMax = it.silenceDetectionAudioLevel > (data[0] as Short),
                            currentVolume = (data[0] as Short).toString(),
                            isRecording = data[1] as Boolean
                        )
                    }
                }
        }


        viewModelScope.launch(Dispatchers.Default) {
            nativeApplication.isAppInBackground.collect { isAppInBackground ->
                if(isAppInBackground) {
                    audioRecorder.stopRecording()
                }
            }
        }
    }

}