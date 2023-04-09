package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import kotlinx.coroutines.flow.update
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingServiceParams
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioServiceParams
import org.rhasspy.mobile.logic.services.mqtt.MqttServiceParams
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.ChangeAudioPlayingHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.ChangeAudioPlayingMqttSiteId
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.SelectAudioOutputOption
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.SelectAudioPlayingOption
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationUiAction.ToggleUseCustomHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.test.AudioPlayingConfigurationTest

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
    testRunner: AudioPlayingConfigurationTest,
    logType: LogType
) : IConfigurationViewModel<AudioPlayingConfigurationTest, AudioPlayingConfigurationViewState>(
    service = service,
    testRunner = testRunner,
    logType = logType,
    initialViewState = AudioPlayingConfigurationViewState.getInitial()
) {

    fun onAction(action: AudioPlayingConfigurationUiAction) {
        when (action) {
            is SelectAudioPlayingOption -> contentViewState.update { it.copy(audioPlayingOption = action.option) }
            is SelectAudioOutputOption -> contentViewState.update { it.copy(audioOutputOption = action.option) }
            ToggleUseCustomHttpEndpoint -> contentViewState.update { it.copy(isUseCustomAudioPlayingHttpEndpoint = !it.isUseCustomAudioPlayingHttpEndpoint) }
            is ChangeAudioPlayingHttpEndpoint -> contentViewState.update { it.copy(audioPlayingHttpEndpoint = action.value) }
            is ChangeAudioPlayingMqttSiteId -> contentViewState.update { it.copy(audioPlayingMqttSiteId = action.value) }
        }
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        contentViewState.value.save()
    }

    /**
     * undo all changes
     */
    override fun discard() {
        contentViewState.value = AudioPlayingConfigurationViewState.getInitial()
    }

    override fun initializeTestParams() {
        get<AudioPlayingServiceParams> {
            parametersOf(
                AudioPlayingServiceParams(
                    audioPlayingOption = contentViewState.value.audioPlayingOption
                )
            )
        }

        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isUseCustomAudioPlayingEndpoint = contentViewState.value.isUseCustomAudioPlayingHttpEndpoint,
                    audioPlayingHttpEndpoint = contentViewState.value.audioPlayingHttpEndpoint
                )
            )
        }

        get<LocalAudioServiceParams> {
            parametersOf(
                LocalAudioServiceParams(
                    audioOutputOption = contentViewState.value.audioOutputOption
                )
            )
        }

        get<MqttServiceParams> {
            parametersOf(
                MqttServiceParams(
                    audioPlayingMqttSiteId = contentViewState.value.audioPlayingMqttSiteId
                )
            )
        }
    }

    fun playTestAudio() = testRunner.playTestAudio()

}