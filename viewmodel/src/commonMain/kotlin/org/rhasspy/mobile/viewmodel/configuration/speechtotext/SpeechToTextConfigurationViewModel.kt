package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.services.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Action.OpenAudioOutputFormat
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Action.OpenAudioRecorderFormat
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.AudioOutputFormatUiEvent
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.AudioOutputFormatUiEvent.Change.SelectAudioOutputChannelType
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.AudioOutputFormatUiEvent.Change.SelectAudioOutputEncodingType
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.AudioOutputFormatUiEvent.Change.SelectAudioOutputSampleRateType
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.AudioRecorderFormatUiEvent
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.AudioRecorderFormatUiEvent.Change.SelectAudioRecorderChannelType
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.AudioRecorderFormatUiEvent.Change.SelectAudioRecorderEncodingType
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.AudioRecorderFormatUiEvent.Change.SelectAudioRecorderSampleRateType
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SelectSpeechToTextOption
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SetUseCustomHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SetUseSpeechToTextMqttSilenceDetection
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.UpdateSpeechToTextHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.SpeechToTextConfigurationScreenDestination.AudioOutputFormatScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.SpeechToTextConfigurationScreenDestination.AudioRecorderFormatScreen

@Stable
class SpeechToTextConfigurationViewModel(
    service: ISpeechToTextService,
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
        configurationViewState: MutableStateFlow<ConfigurationViewState>,
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::SpeechToTextConfigurationData,
            viewState = viewState,
            configurationViewState = configurationViewState
        )
    }

    fun onEvent(event: SpeechToTextConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
            is AudioOutputFormatUiEvent -> onAudioOutputFormatEvent(event)
            is AudioRecorderFormatUiEvent -> onAudioRecorderFormatEvent(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectSpeechToTextOption -> copy(speechToTextOption = change.option)
                    is SetUseCustomHttpEndpoint -> copy(isUseCustomSpeechToTextHttpEndpoint = change.enabled)
                    is SetUseSpeechToTextMqttSilenceDetection -> copy(
                        isUseSpeechToTextMqttSilenceDetection = change.enabled
                    )

                    is UpdateSpeechToTextHttpEndpoint -> copy(speechToTextHttpEndpoint = change.endpoint)
                }
            })
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
            OpenAudioOutputFormat -> navigator.navigate(AudioOutputFormatScreen)
            OpenAudioRecorderFormat -> navigator.navigate(AudioRecorderFormatScreen)
        }
    }

    private fun onAudioRecorderFormatEvent(event: AudioRecorderFormatUiEvent) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                copy(
                    speechToTextAudioRecorderFormatData = when (event) {
                        is SelectAudioRecorderChannelType -> speechToTextAudioRecorderFormatData.copy(
                            audioRecorderChannelType = event.value
                        )

                        is SelectAudioRecorderEncodingType -> speechToTextAudioRecorderFormatData.copy(
                            audioRecorderEncodingType = event.value
                        )

                        is SelectAudioRecorderSampleRateType -> speechToTextAudioRecorderFormatData.copy(
                            audioRecorderSampleRateType = event.value
                        )
                    },
                    //TODO #408
                    speechToTextAudioOutputFormatData = when (event) {
                        is SelectAudioRecorderEncodingType -> speechToTextAudioOutputFormatData.copy(
                            audioOutputEncodingType = event.value
                        )

                        else -> speechToTextAudioOutputFormatData
                    }
                )
            })
        }
    }

    private fun onAudioOutputFormatEvent(event: AudioOutputFormatUiEvent) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                copy(
                    speechToTextAudioOutputFormatData = when (event) {
                        is SelectAudioOutputChannelType -> speechToTextAudioOutputFormatData.copy(
                            audioOutputChannelType = event.value
                        )

                        is SelectAudioOutputEncodingType -> speechToTextAudioOutputFormatData.copy(
                            audioOutputEncodingType = event.value
                        )

                        is SelectAudioOutputSampleRateType -> speechToTextAudioOutputFormatData.copy(
                            audioOutputSampleRateType = event.value
                        )
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
            ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value =
                isUseCustomSpeechToTextHttpEndpoint
            ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value =
                isUseSpeechToTextMqttSilenceDetection
            ConfigurationSetting.speechToTextHttpEndpoint.value = speechToTextHttpEndpoint

            with(speechToTextAudioRecorderFormatData) {
                ConfigurationSetting.speechToTextAudioRecorderChannel.value =
                    audioRecorderChannelType
                ConfigurationSetting.speechToTextAudioRecorderEncoding.value =
                    audioRecorderEncodingType
                ConfigurationSetting.speechToTextAudioRecorderSampleRate.value =
                    audioRecorderSampleRateType
            }

            with(speechToTextAudioOutputFormatData) {
                ConfigurationSetting.speechToTextAudioOutputChannel.value = audioOutputChannelType
                ConfigurationSetting.speechToTextAudioOutputEncoding.value = audioOutputEncodingType
                ConfigurationSetting.speechToTextAudioOutputSampleRate.value =
                    audioOutputSampleRateType
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return when (navigator.topScreen.value) {
            //do navigate sub screens back even if there are changes
            is NavigationDestination.SpeechToTextConfigurationScreenDestination -> false
            else -> super.onBackPressed()
        }
    }

}