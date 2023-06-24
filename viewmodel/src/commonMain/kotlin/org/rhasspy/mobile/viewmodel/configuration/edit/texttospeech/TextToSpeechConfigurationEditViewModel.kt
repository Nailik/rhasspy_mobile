package org.rhasspy.mobile.viewmodel.configuration.edit.texttospeech

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.texttospeech.TextToSpeechConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.edit.texttospeech.TextToSpeechConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.edit.texttospeech.TextToSpeechConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.edit.texttospeech.TextToSpeechConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.texttospeech.TextToSpeechConfigurationViewState.TextToSpeechConfigurationData

@Stable
class TextToSpeechConfigurationEditViewModel(
    service: TextToSpeechService,
    private val viewStateCreator: ConfigurationEditViewStateCreator
) : IConfigurationEditViewModel(
    service = service
) {

    private val initialConfigurationData = TextToSpeechConfigurationData()

    private val _editData = MutableStateFlow(initialConfigurationData)
    private val _viewState = MutableStateFlow(TextToSpeechConfigurationViewState(initialConfigurationData))
    val viewState = combineState(_viewState, _editData) { viewState, editData ->
        viewState.copy(editData = editData)
    }

    override fun initViewStateCreator(
        configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>
    ): StateFlow<ConfigurationEditViewState> {
        return viewStateCreator(
            init = TextToSpeechConfigurationViewState::TextToSpeechConfigurationData,
            editData = _editData,
            configurationEditViewState = configurationEditViewState
        )
    }

    fun onEvent(event: TextToSpeechConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _editData.update {
            when (change) {
                is SelectTextToSpeechOption -> it.copy(textToSpeechOption = change.option)
                is SetUseCustomHttpEndpoint -> it.copy(isUseCustomTextToSpeechHttpEndpoint = change.enabled)
                is UpdateTextToSpeechHttpEndpoint -> it.copy(textToSpeechHttpEndpoint = change.endpoint)
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
            ConfigurationSetting.textToSpeechOption.value = textToSpeechOption
            ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value = isUseCustomTextToSpeechHttpEndpoint
            ConfigurationSetting.textToSpeechHttpEndpoint.value = textToSpeechHttpEndpoint
        }
    }

}