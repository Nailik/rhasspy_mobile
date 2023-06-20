package org.rhasspy.mobile.viewmodel.configuration.edit.audioplaying

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.audioplaying.AudioPlayingConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.edit.audioplaying.AudioPlayingConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.edit.audioplaying.AudioPlayingConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.edit.audioplaying.AudioPlayingConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.audioplaying.AudioPlayingConfigurationViewState.AudioPlayingConfigurationData

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
    service: AudioPlayingService,
    private val viewStateCreator: ConfigurationEditViewStateCreator
) : IConfigurationEditViewModel(
    service = service
) {

    private val initialConfigurationData = AudioPlayingConfigurationData()

    private val _editData = MutableStateFlow(initialConfigurationData)
    private val _viewState = MutableStateFlow(AudioPlayingConfigurationViewState(initialConfigurationData))
    val viewState = combineState(_viewState, _editData) { viewState, editData ->
        viewState.copy(editData = editData)
    }

    override fun initViewStateCreator(
        configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>
    ): StateFlow<ConfigurationEditViewState> {
        return viewStateCreator(
            init = ::AudioPlayingConfigurationData,
            editData = _editData,
            configurationEditViewState = configurationEditViewState
        )
    }

    fun onEvent(change: AudioPlayingConfigurationUiEvent) {
        when (change) {
            is Change -> onChange(change)
            is Action -> onAction(change)
        }
    }

    private fun onChange(change: Change) {
        _editData.update {
            when (change) {
                is SelectAudioPlayingOption -> it.copy(audioPlayingOption = change.option)
                is SelectAudioOutputOption -> it.copy(audioOutputOption = change.option)
                is SetUseCustomHttpEndpoint -> it.copy(isUseCustomAudioPlayingHttpEndpoint = change.enabled)
                is ChangeAudioPlayingHttpEndpoint -> it.copy(audioPlayingHttpEndpoint = change.enabled)
                is ChangeAudioPlayingMqttSiteId -> it.copy(audioPlayingMqttSiteId = change.siteId)
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
            ConfigurationSetting.audioPlayingOption.value = audioPlayingOption
            ConfigurationSetting.audioOutputOption.value = audioOutputOption
            ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value = isUseCustomAudioPlayingHttpEndpoint
            ConfigurationSetting.audioPlayingHttpEndpoint.value = audioPlayingHttpEndpoint
            ConfigurationSetting.audioPlayingMqttSiteId.value = audioPlayingMqttSiteId
        }
    }

}