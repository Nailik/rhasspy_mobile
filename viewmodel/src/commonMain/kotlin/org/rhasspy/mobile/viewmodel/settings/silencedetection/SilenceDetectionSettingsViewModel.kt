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
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Action.ToggleAudioLevelTest
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.silencedetection.SilenceDetectionSettingsUiEvent.Change.*
import kotlin.math.pow

@Stable
class SilenceDetectionSettingsViewModel(
    private val nativeApplication: NativeApplication,
    private val audioRecorder: AudioRecorder,
    viewStateCreator: SilenceDetectionSettingsViewStateCreator
) : KViewModel() {

    val viewState: StateFlow<SilenceDetectionSettingsViewState> = viewStateCreator()

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

            is UpdateSilenceDetectionAudioLevelPercentage ->
                AppSetting.automaticSilenceDetectionAudioLevel.value =
                    audioRecorder.absoluteMaxVolume.pow(change.percentage.toDouble()).toFloat()

            is UpdateSilenceDetectionMinimumTime ->
                AppSetting.automaticSilenceDetectionMinimumTime.value = change.time.toIntOrZero()

            is UpdateSilenceDetectionTime ->
                AppSetting.automaticSilenceDetectionTime.value = change.time.toIntOrZero()
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            ToggleAudioLevelTest -> requireMicrophonePermission {
                if (audioRecorder.isRecording.value) audioRecorder.stopRecording() else audioRecorder.startRecording(
                    audioRecorderChannelType = AppSetting.audioRecorderChannel.value,
                    audioRecorderEncodingType = AppSetting.audioRecorderEncoding.value,
                    audioRecorderSampleRateType = AppSetting.audioRecorderSampleRate.value
                )
            }

            is BackClick -> navigator.onBackPressed()
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            nativeApplication.isAppInBackground.collect { isAppInBackground ->
                if (isAppInBackground) {
                    audioRecorder.stopRecording()
                }
            }
        }
    }

}
