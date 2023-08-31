package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.domains.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.AudioOutputFormatUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.AudioRecorderFormatUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SelectSpeechToTextOption
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SetUseSpeechToTextMqttSilenceDetection
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.SpeechToTextConfigurationScreenDestination.AudioOutputFormatScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.SpeechToTextConfigurationScreenDestination.AudioRecorderFormatScreen

@Stable
class SpeechToTextConfigurationViewModel(
    service: ISpeechToTextService
) : ConfigurationViewModel(
    service = service
) {

    private val _viewState = MutableStateFlow(
        SpeechToTextConfigurationViewState(
            editData = SpeechToTextConfigurationData(),
            isOutputEncodingChangeEnabled = false //TODO #408
        )
    )
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::SpeechToTextConfigurationData,
            viewState = viewState,
            configurationViewState = configurationViewState
        )
    }

    fun onEvent(event: SpeechToTextConfigurationUiEvent) {
        when (event) {
            is Change                     -> onChange(event)
            is Action                     -> onAction(event)
            is AudioOutputFormatUiEvent   -> onAudioOutputFormatEvent(event)
            is AudioRecorderFormatUiEvent -> onAudioRecorderFormatEvent(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectSpeechToTextOption               -> copy(speechToTextOption = change.option)
                    is SetUseSpeechToTextMqttSilenceDetection -> copy(isUseSpeechToTextMqttSilenceDetection = change.enabled)
                }
            })
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick               -> navigator.onBackPressed()
            OpenAudioOutputFormat   -> navigator.navigate(AudioOutputFormatScreen)
            OpenAudioRecorderFormat -> navigator.navigate(AudioRecorderFormatScreen)
        }
    }

    private fun onAudioRecorderFormatEvent(event: AudioRecorderFormatUiEvent) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                copy(
                    speechToTextAudioRecorderData = when (event) {
                        is SelectAudioRecorderChannelType    -> speechToTextAudioRecorderData.copy(audioRecorderChannelType = event.value)
                        is SelectAudioRecorderEncodingType   -> speechToTextAudioRecorderData.copy(audioRecorderEncodingType = event.value)
                        is SelectAudioRecorderSampleRateType -> speechToTextAudioRecorderData.copy(audioRecorderSampleRateType = event.value)
                    },
                    //TODO #408
                    speechToTextAudioOutputData = when (event) {
                        is SelectAudioRecorderEncodingType -> speechToTextAudioOutputData.copy(audioOutputEncodingType = event.value)
                        else                               -> speechToTextAudioOutputData
                    }
                )
            })
        }
    }

    private fun onAudioOutputFormatEvent(event: AudioOutputFormatUiEvent) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                copy(
                    speechToTextAudioOutputData = when (event) {
                        is SelectAudioOutputChannelType    -> speechToTextAudioOutputData.copy(audioOutputChannelType = event.value)
                        is SelectAudioOutputEncodingType   -> speechToTextAudioOutputData.copy(audioOutputEncodingType = event.value)
                        is SelectAudioOutputSampleRateType -> speechToTextAudioOutputData.copy(audioOutputSampleRateType = event.value)
                    }
                )
            })
        }
    }

    override fun onDiscard() {
        _viewState.update { it.copy(editData = SpeechToTextConfigurationData()) }
    }

    override fun onSave() {
        with(_viewState.value.editData) {
            ConfigurationSetting.speechToTextOption.value = speechToTextOption
            ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value = isUseSpeechToTextMqttSilenceDetection

            with(speechToTextAudioRecorderData) {
                ConfigurationSetting.speechToTextAudioRecorderChannel.value = audioRecorderChannelType
                ConfigurationSetting.speechToTextAudioRecorderEncoding.value = audioRecorderEncodingType
                ConfigurationSetting.speechToTextAudioRecorderSampleRate.value = audioRecorderSampleRateType
            }

            with(speechToTextAudioOutputData) {
                ConfigurationSetting.speechToTextAudioOutputChannel.value = audioOutputChannelType
                ConfigurationSetting.speechToTextAudioOutputEncoding.value = audioOutputEncodingType
                ConfigurationSetting.speechToTextAudioOutputSampleRate.value = audioOutputSampleRateType
            }
        }
    }

}