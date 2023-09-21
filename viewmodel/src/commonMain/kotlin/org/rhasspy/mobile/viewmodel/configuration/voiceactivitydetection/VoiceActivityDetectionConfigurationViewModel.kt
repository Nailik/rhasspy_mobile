package org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.data.toLongOrNullOrConstant
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption.Local
import org.rhasspy.mobile.logic.domains.vad.IVadDomain
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionUiEvent.Change.SelectVoiceActivityDetectionOption
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionUiEvent.LocalSilenceDetectionUiEvent.Action.ToggleAudioLevelTest
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionUiEvent.LocalSilenceDetectionUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionViewState.VoiceActivityDetectionConfigurationData
import kotlin.math.pow

@Stable
class VoiceActivityDetectionConfigurationViewModel(
    private val nativeApplication: NativeApplication,
    private val mapper: VoiceActivityDetectionConfigurationDataMapper,
    audioRecorderViewStateCreator: AudioRecorderViewStateCreator,
    service: IVadDomain,
    private val audioRecorder: IAudioRecorder,
) : ConfigurationViewModel(
    serviceState = service.serviceState
) {
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

    private val initialData get() = mapper(ConfigurationSetting.vadDomainData.value)
    private val _viewState = MutableStateFlow(VoiceActivityDetectionViewState(initialData))
    val viewState = _viewState.readOnly

    val audioRecorderViewState = audioRecorderViewStateCreator(viewState)

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::initialData,
            viewState = viewState,
            configurationViewState = configurationViewState
        )
    }

    fun onEvent(event: VoiceActivityDetectionUiEvent) {
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
                    is SelectVoiceActivityDetectionOption -> {
                        if (audioRecorder.isRecording.value && change.option != Local) {
                            stopRecording()
                        }
                        copy(voiceActivityDetectionOption = change.option)
                    }
                }
            })
        }
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

                        is UpdateSilenceDetectionMinimumTime         -> copy(silenceDetectionMinimumTime = change.time.toLongOrNullOrConstant())
                        is UpdateSilenceDetectionTime                -> copy(silenceDetectionTime = change.time.toLongOrNullOrConstant())
                    }
                })
            })
        }
    }

    override fun onDiscard() {
        stopRecording()
        _viewState.update { it.copy(editData = initialData) }
    }

    override fun onSave() {
        stopRecording()
        ConfigurationSetting.vadDomainData.value = mapper(_viewState.value.editData)
        _viewState.update { it.copy(editData = initialData) }
    }

    private fun startRecording() {
        //save to restore later
        wakeWordSetting = AppSetting.isHotWordEnabled.value
        //disable so recording is stopped
        AppSetting.isHotWordEnabled.value = false
        //start this recording
        //TODO use mic domain instead?
        audioRecorder.startRecording(
            audioRecorderSourceType = ConfigurationSetting.micDomainData.value.audioInputSource,
            audioRecorderChannelType = ConfigurationSetting.micDomainData.value.audioInputChannel,
            audioRecorderEncodingType = ConfigurationSetting.micDomainData.value.audioInputEncoding,
            audioRecorderSampleRateType = ConfigurationSetting.micDomainData.value.audioInputSampleRate,
            audioRecorderOutputChannelType = ConfigurationSetting.micDomainData.value.audioOutputChannel,
            audioRecorderOutputEncodingType = ConfigurationSetting.micDomainData.value.audioOutputEncoding,
            audioRecorderOutputSampleRateType = ConfigurationSetting.micDomainData.value.audioOutputSampleRate,
            isUseAutomaticGainControl = ConfigurationSetting.micDomainData.value.isUseAutomaticGainControl,
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