package org.rhasspy.mobile.logic.domains.audioplaying

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Sound
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.httpclient.IHttpClientConnection
import org.rhasspy.mobile.logic.connections.mqtt.IMqttService
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocusService
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioService
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.PlayFinished
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.readOnly

interface IAudioPlayingService : IService {
    fun playAudio(audioSource: AudioSource)
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

    private val logger = LogType.AudioPlayingService.logger()

    private val audioFocusService by inject<IAudioFocusService>()
    private val localAudioService by inject<ILocalAudioService>()
    private val mqttClientService by inject<IMqttService>()
    private val serviceMiddleware by inject<IServiceMiddleware>()
    private val httpClientConnection by inject<IHttpClientConnection>()

    private val _serviceState = MutableStateFlow<ServiceState>(Pending)
    override val serviceState = _serviceState.readOnly

    private var paramsFlow: StateFlow<AudioPlayingServiceParams> = paramsCreator()
    private val params: AudioPlayingServiceParams get() = paramsFlow.value

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            paramsFlow.collect {
                setupState()
            }
        }
    }

    private fun setupState() {
        _serviceState.value = when (params.audioPlayingOption) {
            AudioPlayingOption.Local      -> Success
            AudioPlayingOption.RemoteHTTP -> Success
            AudioPlayingOption.RemoteMQTT -> Success
            AudioPlayingOption.Disabled   -> Disabled
        }
    }

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
    override fun playAudio(audioSource: AudioSource) {
        logger.d { "playAudio" }
        when (params.audioPlayingOption) {
            AudioPlayingOption.Local      -> {
                audioFocusService.request(Sound)
                localAudioService.playAudio(audioSource) {
                    _serviceState.value = it
                    audioFocusService.abandon(Sound)
                    serviceMiddleware.action(PlayFinished(Source.Local))
                }
            }

            AudioPlayingOption.RemoteHTTP -> {
                httpClientConnection.playWav(audioSource) {
                    _serviceState.value = it.toServiceState()
                    serviceMiddleware.action(PlayFinished(Source.Local))
                }
            }

            AudioPlayingOption.RemoteMQTT -> {
                mqttClientService.playAudioRemote(audioSource) {
                    _serviceState.value = it
                }
                serviceMiddleware.action(PlayFinished(Source.Local))
            }

            AudioPlayingOption.Disabled   -> serviceMiddleware.action(PlayFinished(Source.Local))
        }
    }

    /**
     * stops playing audio when aduio is played locally
     */
    override fun stopPlayAudio() {
        when (params.audioPlayingOption) {
            AudioPlayingOption.Local -> localAudioService.stop()
            else                     -> serviceMiddleware.action(PlayFinished(Source.Local))
        }
    }

}