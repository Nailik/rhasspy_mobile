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
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SelectSpeechToTextOption
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SetUseCustomHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SetUseSpeechToTextMqttSilenceDetection
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.UpdateSpeechToTextHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData

@Stable
class SpeechToTextConfigurationViewModel(
    service: ISpeechToTextService
) : ConfigurationViewModel(
    service = service
) {

    private val _viewState =
        MutableStateFlow(SpeechToTextConfigurationViewState(SpeechToTextConfigurationData()))
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
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectSpeechToTextOption               -> copy(speechToTextOption = change.option)
                    is SetUseCustomHttpEndpoint               -> copy(
                        isUseCustomSpeechToTextHttpEndpoint = change.enabled
                    )

                    is SetUseSpeechToTextMqttSilenceDetection -> copy(
                        isUseSpeechToTextMqttSilenceDetection = change.enabled
                    )

                    is UpdateSpeechToTextHttpEndpoint         -> copy(speechToTextHttpEndpoint = change.endpoint)
                }
            })
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
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
        }
    }

}