package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.domains.tts.ITtsDomain
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiEvent.Change.SelectTextToSpeechOption
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewState.TextToSpeechConfigurationData

@Stable
class TextToSpeechConfigurationViewModel(
    private val mapper: TextToSpeechConfigurationDataMapper,
    service: ITtsDomain
) : ConfigurationViewModel(
    serviceState = service.serviceState
) {

    private val initialData get() = mapper(ConfigurationSetting.ttsDomainData.value)
    private val _viewState = MutableStateFlow(TextToSpeechConfigurationViewState(initialData))
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::initialData,
            viewState = viewState,
            configurationViewState = configurationViewState
        )
    }

    fun onEvent(event: TextToSpeechConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectTextToSpeechOption -> copy(textToSpeechOption = change.option)
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
        _viewState.update { it.copy(editData = initialData) }
    }

    override fun onSave() {
        ConfigurationSetting.ttsDomainData.value = mapper(_viewState.value.editData)
        _viewState.update { it.copy(editData = initialData) }
    }

}