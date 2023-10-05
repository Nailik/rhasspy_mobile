package org.rhasspy.mobile.viewmodel.configuration.snd

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.data.takeInt
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.snd.AudioPlayingConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.snd.AudioPlayingConfigurationUiEvent.Change.*
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
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectEditAudioPlayingOption     -> copy(sndDomainOption = change.option)
                    is SelectAudioOutputOption          -> copy(audioOutputOption = change.option)
                    is ChangeEditAudioPlayingMqttSiteId -> copy(audioPlayingMqttSiteId = change.siteId)
                    is UpdateAudioTimeout               -> copy(audioTimeout = change.timeout.takeInt())
                    is UpdateRhasspy2HermesMqttTimeout  -> copy(rhasspy2HermesMqttTimeout = change.timeout.takeInt())
                }
            })
        }
        ConfigurationSetting.sndDomainData.value = mapper(_viewState.value.editData)
    }

}