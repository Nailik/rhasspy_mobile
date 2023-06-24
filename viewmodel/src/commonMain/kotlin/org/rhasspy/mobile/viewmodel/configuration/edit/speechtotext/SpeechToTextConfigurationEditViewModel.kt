package org.rhasspy.mobile.viewmodel.configuration.edit.speechtotext

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.speechtotext.SpeechToTextConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.edit.speechtotext.SpeechToTextConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.edit.speechtotext.SpeechToTextConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.edit.speechtotext.SpeechToTextConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData

@Stable
class SpeechToTextConfigurationEditViewModel(
    service: SpeechToTextService,
    private val viewStateCreator: ConfigurationEditViewStateCreator
) : IConfigurationEditViewModel(
    service = service
) {

    private val initialConfigurationData = SpeechToTextConfigurationData()

    private val _editData = MutableStateFlow(initialConfigurationData)
    private val _viewState = MutableStateFlow(SpeechToTextConfigurationViewState(initialConfigurationData))
    val viewState = combineState(_viewState, _editData) { viewState, editData ->
        viewState.copy(editData = editData)
    }

    override fun initViewStateCreator(
        configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>
    ): StateFlow<ConfigurationEditViewState> {
        return viewStateCreator(
            init = ::SpeechToTextConfigurationData,
            editData = _editData,
            configurationEditViewState = configurationEditViewState
        )
    }

    fun onEvent(event: SpeechToTextConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _editData.update {
            when (change) {
                is SelectSpeechToTextOption -> it.copy(speechToTextOption = change.option)
                is SetUseCustomHttpEndpoint -> it.copy(isUseCustomSpeechToTextHttpEndpoint = change.enabled)
                is SetUseSpeechToTextMqttSilenceDetection -> it.copy(isUseSpeechToTextMqttSilenceDetection = change.enabled)
                is UpdateSpeechToTextHttpEndpoint -> it.copy(speechToTextHttpEndpoint = change.endpoint)
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
        }
    }

    override fun onDiscard() {}

    override fun onSave() {
        with(_editData.value) {
            ConfigurationSetting.speechToTextOption.value = speechToTextOption
            ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value = isUseCustomSpeechToTextHttpEndpoint
            ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value = isUseSpeechToTextMqttSilenceDetection
            ConfigurationSetting.speechToTextHttpEndpoint.value = speechToTextHttpEndpoint
        }
    }

}