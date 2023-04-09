package org.rhasspy.mobile.logic.services.audioplaying

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.middleware.Action.DialogAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
open class AudioPlayingService : IService() {
    private val logger = LogType.AudioPlayingService.logger()

    private val params by inject<AudioPlayingServiceParams>()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success)
    override val serviceState = _serviceState.readOnly

    private val localAudioService by inject<LocalAudioService>()
    private val httpClientService by inject<HttpClientService>()
    private val mqttClientService by inject<MqttService>()

    private val serviceMiddleware by inject<ServiceMiddleware>()

    /**
     * hermes/audioServer/<siteId>/playBytes/<requestId>
     * Play WAV data
     *
     * Response(s)
     * hermes/audioServer/<siteId>/playFinished (JSON)
     *
     * - if audio output is enabled
     *
     * Local:
     * - play audio with volume set
     *
     * HTTP:
     * - calls service to play audio with wav data
     *
     * MQTT:
     * - calls default site to play audio
     */
    suspend fun playAudio(audioSource: AudioSource) {
        logger.d { "playAudio dataSize: $audioSource" }
        when (params.audioPlayingOption) {
            AudioPlayingOption.Local -> {
                _serviceState.value = localAudioService.playAudio(audioSource)
                serviceMiddleware.action(DialogAction.PlayFinished(Source.Local))
            }

            AudioPlayingOption.RemoteHTTP -> {
                _serviceState.value = httpClientService.playWav(audioSource).toServiceState()
                serviceMiddleware.action(DialogAction.PlayFinished(Source.HttpApi))
            }

            AudioPlayingOption.RemoteMQTT -> {
                _serviceState.value = mqttClientService.playAudioRemote(audioSource)
                serviceMiddleware.action(DialogAction.PlayFinished(Source.Local))
            }

            AudioPlayingOption.Disabled -> {}
        }
    }

    /**
     * stops playing audio when aduio is played locally
     */
    fun stopPlayAudio() {
        when (params.audioPlayingOption) {
            AudioPlayingOption.Local -> localAudioService.stop()
            else -> {}
        }
    }

}