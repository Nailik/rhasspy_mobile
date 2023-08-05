package org.rhasspy.mobile.viewmodel.settings.silencedetection

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.toLongOrNullOrConstant
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting
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
    private val audioRecorder: IAudioRecorder,
) : ScreenViewModel() {

    private val dispatcher by inject<IDispatcherProvider>()

    val viewState: StateFlow<SilenceDetectionSettingsViewState> = viewStateCreator()

    init {
        viewModelScope.launch(dispatcher.IO) {
            nativeApplication.isAppInBackground.collect { isAppInBackground ->
                if (isAppInBackground) {
                    audioRecorder.stopRecording()
                }
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
            is SetSilenceDetectionEnabled                ->
                AppSetting.isAutomaticSilenceDetectionEnabled.value = change.enabled

            is UpdateSilenceDetectionAudioLevelLogarithm ->
                AppSetting.automaticSilenceDetectionAudioLevel.value =
                    if (change.percentage != 0f) {
                        audioRecorder.absoluteMaxVolume.pow(change.percentage)
                    } else 0f

            is UpdateSilenceDetectionMinimumTime         ->
                AppSetting.automaticSilenceDetectionMinimumTime.value = change.time.toLongOrNullOrConstant()

            is UpdateSilenceDetectionTime                ->
                AppSetting.automaticSilenceDetectionTime.value = change.time.toLongOrNullOrConstant()
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            ToggleAudioLevelTest -> requireMicrophonePermission {
                if (audioRecorder.isRecording.value) audioRecorder.stopRecording()
                else audioRecorder.startRecording(
                    audioRecorderChannelType = ConfigurationSetting.speechToTextAudioRecorderChannel.value,
                    audioRecorderEncodingType = ConfigurationSetting.speechToTextAudioRecorderEncoding.value,
                    audioRecorderSampleRateType = ConfigurationSetting.speechToTextAudioRecorderSampleRate.value
                )
            }

            is BackClick         -> navigator.onBackPressed()
        }
    }

}
