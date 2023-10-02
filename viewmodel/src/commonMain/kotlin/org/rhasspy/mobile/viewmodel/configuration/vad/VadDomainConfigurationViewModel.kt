package org.rhasspy.mobile.viewmodel.configuration.vad

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.data.toLongOrZero
import org.rhasspy.mobile.data.service.option.VadDomainOption.Local
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainUiEvent.Change.SelectVadDomainOption
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainUiEvent.LocalSilenceDetectionUiEvent.Action.ToggleAudioLevelTest
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainUiEvent.LocalSilenceDetectionUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

@Stable
class VadDomainConfigurationViewModel(
    private val nativeApplication: NativeApplication,
    private val mapper: VadDomainConfigurationDataMapper,
    audioRecorderViewStateCreator: AudioRecorderViewStateCreator,
    private val audioRecorder: IAudioRecorder,
) : ScreenViewModel() {

    private val dispatcher by inject<IDispatcherProvider>()
    private var wakeWordSetting = AppSetting.isHotWordEnabled.value

    init {
        viewModelScope.launch(dispatcher.IO) {
            nativeApplication.isAppInBackground.collect { isAppInBackground ->
                if (isAppInBackground) {
                    stopRecording()
                }
            }
        }
    }

    private val _viewState = MutableStateFlow(VadDomainViewState(mapper(ConfigurationSetting.vadDomainData.value)))
    val viewState = _viewState.readOnly

    val audioRecorderViewState = audioRecorderViewStateCreator(viewState)

    fun onEvent(event: VadDomainUiEvent) {
        when (event) {
            is Change                       -> onChange(event)
            is Action                       -> onAction(event)
            is LocalSilenceDetectionUiEvent -> onLocalSilenceDetectionUiEvent(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectVadDomainOption -> {
                        if (audioRecorder.isRecording.value && change.option != Local) {
                            stopRecording()
                        }
                        copy(vadDomainOption = change.option)
                    }
                }
            })
        }
        ConfigurationSetting.vadDomainData.value = mapper(_viewState.value.editData)
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
        }
    }

    private fun onLocalSilenceDetectionUiEvent(event: LocalSilenceDetectionUiEvent) {
        when (event) {
            is LocalSilenceDetectionUiEvent.Action -> onLocalSilenceDetectionAction(event)
            is LocalSilenceDetectionUiEvent.Change -> onLocalSilenceDetectionChange(event)
        }
    }

    private fun onLocalSilenceDetectionAction(action: LocalSilenceDetectionUiEvent.Action) {
        when (action) {
            ToggleAudioLevelTest -> requireMicrophonePermission {
                if (audioRecorder.isRecording.value) {
                    stopRecording()
                } else {
                    startRecording()
                }
            }
        }
    }

    private fun onLocalSilenceDetectionChange(change: LocalSilenceDetectionUiEvent.Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                copy(localSilenceDetectionSetting = with(localSilenceDetectionSetting) {
                    when (change) {
                        is UpdateSilenceDetectionAudioLevelLogarithm -> copy(
                            silenceDetectionAudioLevel = if (change.percentage != 0f) {
                                audioRecorder.absoluteMaxVolume.pow(change.percentage)
                            } else 0f
                        )

                        is UpdateSilenceDetectionMinimumTime         -> copy(silenceDetectionMinimumTime = change.time.toLongOrZero().milliseconds)
                        is UpdateSilenceDetectionTime                -> copy(silenceDetectionTime = change.time.toLongOrZero().milliseconds)
                    }
                })
            })
        }
        ConfigurationSetting.vadDomainData.value = mapper(_viewState.value.editData)
    }

    private fun startRecording() {
        //save to restore later
        wakeWordSetting = AppSetting.isHotWordEnabled.value
        //disable so recording is stopped
        AppSetting.isHotWordEnabled.value = false
        //start this recording
        audioRecorder.startRecording(
            audioRecorderSourceType = ConfigurationSetting.micDomainData.value.audioInputSource,
            audioRecorderChannelType = ConfigurationSetting.micDomainData.value.audioInputChannel,
            audioRecorderEncodingType = ConfigurationSetting.micDomainData.value.audioInputEncoding,
            audioRecorderSampleRateType = ConfigurationSetting.micDomainData.value.audioInputSampleRate,
            audioRecorderOutputChannelType = ConfigurationSetting.micDomainData.value.audioOutputChannel,
            audioRecorderOutputEncodingType = ConfigurationSetting.micDomainData.value.audioOutputEncoding,
            audioRecorderOutputSampleRateType = ConfigurationSetting.micDomainData.value.audioOutputSampleRate,
            isAutoPauseOnMediaPlayback = ConfigurationSetting.micDomainData.value.isPauseRecordingOnMediaPlayback,
        )
    }

    private fun stopRecording() {
        audioRecorder.stopRecording()
        //reset to previous setting
        AppSetting.isHotWordEnabled.value = wakeWordSetting
    }

    override fun onDismissed() {
        stopRecording()
        super.onDismissed()
    }

}