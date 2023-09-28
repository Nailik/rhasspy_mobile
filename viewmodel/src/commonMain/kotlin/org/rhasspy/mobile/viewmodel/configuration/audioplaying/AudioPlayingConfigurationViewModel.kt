package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

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
    private val mapper: AudioPlayingConfigurationDataMapper,
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(AudioPlayingConfigurationViewState(mapper(ConfigurationSetting.sndDomainData.value)))
    val viewState = _viewState.readOnly

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
        ConfigurationSetting.sndDomainData.value = mapper(_viewState.value.editData)
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
        }
    }

}