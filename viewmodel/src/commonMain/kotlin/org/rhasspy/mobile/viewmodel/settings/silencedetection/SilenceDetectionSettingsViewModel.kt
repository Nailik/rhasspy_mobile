package org.rhasspy.mobile.viewmodel.settings.silencedetection

import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorder
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Action.ToggleAudioLevelTest
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Change.*
import kotlin.math.pow

@Stable
class SilenceDetectionSettingsViewModel(
    private val nativeApplication: NativeApplication,
    viewStateCreator: SilenceDetectionSettingsViewStateCreator,
    private val audioRecorder: AudioRecorder,
) : ScreenViewModel() {

    val viewState: StateFlow<SilenceDetectionSettingsViewState> = viewStateCreator()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            nativeApplication.isAppInBackground.collect { isAppInBackground ->
                if (isAppInBackground) {
                    audioRecorder.stopRecording()
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            audioRecorder.isRecording.collect { isAppInBackground ->
                println(isAppInBackground)
            }
        }
    }

    fun onEvent(event: SilenceDetectionSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SetSilenceDetectionEnabled ->
                AppSetting.isAutomaticSilenceDetectionEnabled.value = change.enabled

            is UpdateSilenceDetectionAudioLevelLogarithm ->
                AppSetting.automaticSilenceDetectionAudioLevel.value = if (change.percentage != 0f) {
                    audioRecorder.absoluteMaxVolume.pow(change.percentage)
                } else 0f

            is UpdateSilenceDetectionMinimumTime ->
                AppSetting.automaticSilenceDetectionMinimumTime.value = change.time.toIntOrZero()

            is UpdateSilenceDetectionTime ->
                AppSetting.automaticSilenceDetectionTime.value = change.time.toIntOrZero()
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            ToggleAudioLevelTest -> requireMicrophonePermission {
                if (audioRecorder.isRecording.value) audioRecorder.stopRecording()
                else audioRecorder.startRecording(
                    audioRecorderChannelType = AppSetting.audioRecorderChannel.value,
                    audioRecorderEncodingType = AppSetting.audioRecorderEncoding.value,
                    audioRecorderSampleRateType = AppSetting.audioRecorderSampleRate.value
                )
            }

            is BackClick -> navigator.onBackPressed()
        }
    }

}
