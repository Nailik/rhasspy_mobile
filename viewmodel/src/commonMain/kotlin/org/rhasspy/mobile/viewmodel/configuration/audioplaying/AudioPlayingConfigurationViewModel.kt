package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.domains.audioplaying.ISndDomain
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewState.AudioPlayingConfigurationData

/**
 * ViewModel for Audio Playing Configuration
 *
 * Current Option
 * Endpoint value
 * if Endpoint option should be shown
 * all Options as list
 */
@Stable
class AudioPlayingConfigurationViewModel(
    service: ISndDomain
) : ConfigurationViewModel(
    serviceState = service.serviceState
) {

    private val _viewState = MutableStateFlow(AudioPlayingConfigurationViewState(AudioPlayingConfigurationData()))
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::AudioPlayingConfigurationData,
            viewState = viewState,
            configurationViewState = configurationViewState
        )
    }

    fun onEvent(change: AudioPlayingConfigurationUiEvent) {
        when (change) {
            is Change -> onChange(change)
            is Action -> onAction(change)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectEditAudioPlayingOption     -> copy(audioPlayingOption = change.option)
                    is SelectAudioOutputOption          -> copy(audioOutputOption = change.option)
                    is ChangeEditAudioPlayingMqttSiteId -> copy(audioPlayingMqttSiteId = change.siteId)
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
        _viewState.update { it.copy(editData = AudioPlayingConfigurationData()) }
    }

    override fun onSave() {
        with(_viewState.value.editData) {
            ConfigurationSetting.audioPlayingOption.value = audioPlayingOption
            ConfigurationSetting.audioOutputOption.value = audioOutputOption
            ConfigurationSetting.audioPlayingMqttSiteId.value = audioPlayingMqttSiteId
        }
    }

}