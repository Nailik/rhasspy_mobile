package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.domains.asr.IAsrDomain
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationDataMapper
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SelectSpeechToTextOption
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SetUseSpeechToTextMqttSilenceDetection
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData

@Stable
class SpeechToTextConfigurationViewModel(
    private val mapper: SpeechToTextConfigurationDataMapper,
    service: IAsrDomain
) : ConfigurationViewModel(
    serviceState = service.serviceState
) {

    private val initialData get() = mapper(ConfigurationSetting.asrDomainData.value)
    private val _viewState = MutableStateFlow(SpeechToTextConfigurationViewState(editData = initialData,))
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
                    is SetUseSpeechToTextMqttSilenceDetection -> copy(isUseSpeechToTextMqttSilenceDetection = change.enabled)
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
        ConfigurationSetting.asrDomainData.value = mapper(_viewState.value.editData)
        _viewState.update { it.copy(editData = initialData) }
    }

    override fun onBackPressed(): Boolean {
        return when (navigator.topScreen.value) {
            //do navigate sub screens back even if there are changes
            is NavigationDestination.SpeechToTextConfigurationScreenDestination -> false
            else                                                                -> super.onBackPressed()
        }
    }

}