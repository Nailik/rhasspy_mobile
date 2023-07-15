package org.rhasspy.mobile.logic.services.audioplaying

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Sound
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.audiofocus.IAudioFocusService
import org.rhasspy.mobile.logic.services.httpclient.IHttpClientService
import org.rhasspy.mobile.logic.services.localaudio.ILocalAudioService
import org.rhasspy.mobile.logic.services.mqtt.IMqttService
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.readOnly

interface IAudioPlayingService : IService {
    suspend fun playAudio(audioSource: AudioSource)
    fun stopPlayAudio()
}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class AudioPlayingService(
    paramsCreator: AudioPlayingServiceParamsCreator
) : IAudioPlayingService {

    override val logger = LogType.AudioPlayingService.logger()

    private val audioFocusService by inject<IAudioFocusService>()
    private val localAudioService by inject<ILocalAudioService>()
    private val httpClientService by inject<IHttpClientService>()
    private val mqttClientService by inject<IMqttService>()
    private val serviceMiddleware by inject<IServiceMiddleware>()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success)
    override val serviceState = _serviceState.readOnly

    private var paramsFlow: StateFlow<AudioPlayingServiceParams> = paramsCreator()
    private val params: AudioPlayingServiceParams get() = paramsFlow.value

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
    override suspend fun playAudio(audioSource: AudioSource) {
        logger.d { "playAudio dataSize: $audioSource" }
        when (params.audioPlayingOption) {
            AudioPlayingOption.Local -> {
                audioFocusService.request(Sound)
                _serviceState.value = localAudioService.playAudio(audioSource)
                audioFocusService.abandon(Sound)
                serviceMiddleware.action(DialogServiceMiddlewareAction.PlayFinished(Source.Local))
            }

            AudioPlayingOption.RemoteHTTP -> {
                _serviceState.value = httpClientService.playWav(audioSource).toServiceState()
                serviceMiddleware.action(DialogServiceMiddlewareAction.PlayFinished(Source.HttpApi))
            }

            AudioPlayingOption.RemoteMQTT -> {
                _serviceState.value = mqttClientService.playAudioRemote(audioSource)
                serviceMiddleware.action(DialogServiceMiddlewareAction.PlayFinished(Source.Local))
            }

            AudioPlayingOption.Disabled -> Unit
        }
    }

    /**
     * stops playing audio when aduio is played locally
     */
    override fun stopPlayAudio() {
        when (params.audioPlayingOption) {
            AudioPlayingOption.Local -> localAudioService.stop()
            else -> Unit
        }
    }

}