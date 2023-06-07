package org.rhasspy.mobile.viewmodel.configuration.edit.audioplaying

import androidx.compose.runtime.Stable
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.audioplaying.AudioPlayingConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.edit.audioplaying.AudioPlayingConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.edit.audioplaying.AudioPlayingConfigurationUiEvent.Action.PlayTestAudio
import org.rhasspy.mobile.viewmodel.configuration.edit.audioplaying.AudioPlayingConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.edit.audioplaying.AudioPlayingConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.AudioPlayingConfigurationScreenDestination.EditScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.AudioPlayingConfigurationScreenDestination.TestScreen

/**
 * ViewModel for Audio Playing Configuration
 *
 * Current Option
 * Endpoint value
 * if Endpoint option should be shown
 * all Options as list
 */
@Stable
class AudioPlayingConfigurationEditViewModel(
    service: AudioPlayingService
) : IConfigurationEditViewModel<AudioPlayingConfigurationViewState>(
    service = service,
    initialViewState = ::AudioPlayingConfigurationViewState,
    testPageDestination = TestScreen
) {

    val screen = navigator.topScreen(EditScreen)

    fun onEvent(change: AudioPlayingConfigurationUiEvent) {
        when (change) {
            is Change -> onChange(change)
            is Action -> onAction(change)
        }
    }

    private fun onChange(change: Change) {
        updateViewState {
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
            PlayTestAudio -> playTestAudio()
            BackClick -> navigator.onBackPressed()
        }
    }

    override fun onDiscard() {}

    override fun onSave() {
        ConfigurationSetting.audioPlayingOption.value = data.audioPlayingOption
        ConfigurationSetting.audioOutputOption.value = data.audioOutputOption
        ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value = data.isUseCustomAudioPlayingHttpEndpoint
        ConfigurationSetting.audioPlayingHttpEndpoint.value = data.audioPlayingHttpEndpoint
        ConfigurationSetting.audioPlayingMqttSiteId.value = data.audioPlayingMqttSiteId
    }

    private fun playTestAudio() {
        testScope.launch {
            if (ConfigurationSetting.audioPlayingOption.value == AudioPlayingOption.RemoteMQTT) {
                //await for mqtt service to start if necessary
                awaitMqttServiceStarted()
            }
            get<AudioPlayingService>().playAudio(AudioSource.Resource(MR.files.etc_wav_beep_hi))
        }
    }

}