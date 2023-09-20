package org.rhasspy.mobile.logic.domains.snd

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioPlayer
import org.rhasspy.mobile.logic.pipeline.IPipeline
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.AudioDomainEvent.*
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.SndDomainEvent.PlayedEvent
import org.rhasspy.mobile.settings.ConfigurationSetting

interface ISndDomain : IService {

    fun onAudioStart(audioStart: AudioStartEvent)

    fun onAudioChunk(chunk: AudioChunkEvent)

    fun onAudioStop(stop: AudioStopEvent)

    fun stopPlayAudio()

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class SndDomain(
    private val pipeline: IPipeline,
    private val audioFocusService: IAudioFocus,
    private val localAudioService: ILocalAudioPlayer,
    private val mqttClientService: IMqttConnection,
    private val httpClientConnection: IRhasspy2HermesConnection,
) : ISndDomain {

    private val logger = Logger.withTag("AudioPlayingService")

    override val serviceState = MutableStateFlow<ServiceState>(Pending)
    private val params get() = ConfigurationSetting.sndDomainData.value


    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            ConfigurationSetting.sndDomainData.data.collectLatest {
                initialize()
            }
        }
    }

    private fun initialize() {
        serviceState.value = when (params.option) {
            AudioPlayingOption.Local              -> Success
            AudioPlayingOption.Rhasspy2HermesHttp -> Success
            AudioPlayingOption.Rhasspy2HermesMQTT -> Success
            AudioPlayingOption.Disabled           -> Disabled
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
   /* override fun playAudio(play: PlayEvent) {
        logger.d { "playAudio" }
        serviceState.value = when (params.option) {
            AudioPlayingOption.Local -> {
                audioFocusService.request(Sound)
                localAudioService.playAudio(
                    audioSource = AudioSource.Data(play.data),
                    audioOutputOption = params.localOutputOption,
                    onFinished = {
                        serviceState.value = it
                        audioFocusService.abandon(Sound)
                        pipeline.onEvent(PlayedEvent)
                    }
                )
                Loading
            }

            AudioPlayingOption.Rhasspy2HermesHttp -> {
                httpClientConnection.playWav(
                    audioSource = AudioSource.Data(play.data),
                    onResult = {
                        serviceState.value = it.toServiceState()
                        pipeline.onEvent(PlayedEvent)
                    }
                )
                Loading
            }

            AudioPlayingOption.Rhasspy2HermesMQTT -> {
                mqttClientService.playAudioRemote(
                    audioSource = AudioSource.Data(play.data),
                    siteId = params.mqttSiteId,
                    onResult = {
                        serviceState.value = it
                        pipeline.onEvent(PlayedEvent)
                    }
                )
                Loading
            }

            AudioPlayingOption.Disabled -> {
                pipeline.onEvent(PlayedEvent)
                Disabled
            }
        }
    }*/

    override fun onAudioStart(audioStart: AudioStartEvent) {
        //TODO("Not yet implemented")
    }

    override fun onAudioChunk(chunk: AudioChunkEvent) {
        // TODO("Not yet implemented")
    }

    override fun onAudioStop(stop: AudioStopEvent) {
        // TODO("Not yet implemented")
        pipeline.onEvent(PlayedEvent)
    }

    /**
     * stops playing audio when audio is played locally
     */
    override fun stopPlayAudio() {
        when (params.option) {
            AudioPlayingOption.Local -> localAudioService.stop()
            else                     -> pipeline.onEvent(PlayedEvent)
        }
    }

}