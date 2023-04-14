package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import kotlinx.coroutines.flow.update
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingServiceParams
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioServiceParams
import org.rhasspy.mobile.logic.services.mqtt.MqttServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.ChangeAudioPlayingHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.ChangeAudioPlayingMqttSiteId
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.SelectAudioOutputOption
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.SelectAudioPlayingOption
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.ToggleUseCustomHttpEndpoint

/**
 * ViewModel for Audio Playing Configuration
 *
 * Current Option
 * Endpoint value
 * if Endpoint option should be shown
 * all Options as list
 */
class AudioPlayingConfigurationViewModel(
    service: AudioPlayingService,
    testRunner: AudioPlayingConfigurationTest
) : IConfigurationViewModel<AudioPlayingConfigurationTest, AudioPlayingConfigurationViewState>(
    service = service,
    testRunner = testRunner,
    initialViewState = ::AudioPlayingConfigurationViewState
) {

    fun onAction(action: AudioPlayingConfigurationUiAction) {
        contentViewState.update {
            when (action) {
                is SelectAudioPlayingOption -> it.copy(audioPlayingOption = action.option)
                is SelectAudioOutputOption -> it.copy(audioOutputOption = action.option)
                ToggleUseCustomHttpEndpoint -> it.copy(isUseCustomAudioPlayingHttpEndpoint = !it.isUseCustomAudioPlayingHttpEndpoint)
                is ChangeAudioPlayingHttpEndpoint -> it.copy(audioPlayingHttpEndpoint = action.value)
                is ChangeAudioPlayingMqttSiteId -> it.copy(audioPlayingMqttSiteId = action.value)
            }
        }
    }

    override fun onSave() {
        ConfigurationSetting.audioPlayingOption.value = data.audioPlayingOption
        ConfigurationSetting.audioOutputOption.value = data.audioOutputOption
        ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value = data.isUseCustomAudioPlayingHttpEndpoint
        ConfigurationSetting.audioPlayingHttpEndpoint.value = data.audioPlayingHttpEndpoint
        ConfigurationSetting.audioPlayingMqttSiteId.value = data.audioPlayingMqttSiteId
    }

    override fun initializeTestParams() {
        get<AudioPlayingServiceParams> {
            parametersOf(
                AudioPlayingServiceParams(
                    audioPlayingOption = data.audioPlayingOption
                )
            )
        }

        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isUseCustomAudioPlayingEndpoint = data.isUseCustomAudioPlayingHttpEndpoint,
                    audioPlayingHttpEndpoint = data.audioPlayingHttpEndpoint
                )
            )
        }

        get<LocalAudioServiceParams> {
            parametersOf(
                LocalAudioServiceParams(
                    audioOutputOption = data.audioOutputOption
                )
            )
        }

        get<MqttServiceParams> {
            parametersOf(
                MqttServiceParams(
                    audioPlayingMqttSiteId = data.audioPlayingMqttSiteId
                )
            )
        }
    }

    fun playTestAudio() = testRunner.playTestAudio()

}