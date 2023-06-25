package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
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
    service: AudioPlayingService
) : IConfigurationViewModel(
    service = service
) {

    private val initialConfigurationData = AudioPlayingConfigurationData()

    private val _editData = MutableStateFlow(initialConfigurationData)
    private val _viewState = MutableStateFlow(AudioPlayingConfigurationViewState(initialConfigurationData))
    val viewState = combineState(_viewState, _editData) { viewState, editData ->
        viewState.copy(editData = editData)
    }

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<IConfigurationViewState>
    ): StateFlow<IConfigurationViewState> {
        return viewStateCreator(
            init = ::AudioPlayingConfigurationData,
            editData = _editData,
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
        _editData.update {
            when (change) {
                is SelectEditAudioPlayingOption -> it.copy(audioPlayingOption = change.option)
                is SelectAudioOutputOption -> it.copy(audioOutputOption = change.option)
                is SetUseCustomHttpEndpoint -> it.copy(isUseCustomAudioPlayingHttpEndpoint = change.enabled)
                is ChangeEditAudioPlayingHttpEndpoint -> it.copy(audioPlayingHttpEndpoint = change.enabled)
                is ChangeEditAudioPlayingMqttSiteId -> it.copy(audioPlayingMqttSiteId = change.siteId)
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