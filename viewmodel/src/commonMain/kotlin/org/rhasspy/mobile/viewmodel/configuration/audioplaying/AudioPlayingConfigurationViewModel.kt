package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingServiceParams
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Action.PlayTestAudio
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.navigation.Navigator

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
    navigator: Navigator
) : IConfigurationViewModel<AudioPlayingConfigurationViewState>(
    service = service,
    initialViewState = ::AudioPlayingConfigurationViewState,
    navigator = navigator
) {

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
            if (get<AudioPlayingServiceParams>().audioPlayingOption == AudioPlayingOption.RemoteMQTT) {
                //await for mqtt service to start if necessary
                get<MqttService>()
                    .isHasStarted
                    .map { it }
                    .distinctUntilChanged()
                    .first { it }
            }
            get<AudioPlayingService>().playAudio(AudioSource.Resource(MR.files.etc_wav_beep_hi))
        }
    }

}