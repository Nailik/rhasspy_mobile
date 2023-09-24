package org.rhasspy.mobile.logic.domains.snd

import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Sound
import org.rhasspy.mobile.data.domain.SndDomainData
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
import org.rhasspy.mobile.logic.pipeline.SndResult
import org.rhasspy.mobile.logic.pipeline.SndResult.Played
import org.rhasspy.mobile.logic.pipeline.TtsResult
import org.rhasspy.mobile.logic.pipeline.TtsResult.Audio
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.settings.ConfigurationSetting

interface ISndDomain : IService {

    //plays until play stop
    suspend fun onPlayAudio(audio: Audio, volume: Float?, siteId: String): SndResult

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class SndDomain(
    private val params: SndDomainData,
    private val audioFocusService: IAudioFocus,
    private val localAudioService: ILocalAudioPlayer,
    private val mqttClientService: IMqttConnection,
    private val httpClientConnection: IRhasspy2HermesConnection,
) : ISndDomain {

    private val logger = Logger.withTag("AudioPlayingService")

    override val serviceState = MutableStateFlow<ServiceState>(Pending)

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        serviceState.value = when (params.option) {
            AudioPlayingOption.Local              -> Success
            AudioPlayingOption.Rhasspy2HermesHttp -> Success
            AudioPlayingOption.Rhasspy2HermesMQTT -> Success
            AudioPlayingOption.Disabled           -> Disabled
        }
    }

    override suspend fun onPlayAudio(audio: Audio, volume: Float?, siteId: String):SndResult {
        return when (params.option) {
            AudioPlayingOption.Local              -> onLocalPlayAudio(audio, volume)
            AudioPlayingOption.Rhasspy2HermesHttp -> onRhasspy2HermesHttpPlayAudio(audio, volume, siteId)
            AudioPlayingOption.Rhasspy2HermesMQTT -> onRhasspy2HermesMQTTPlayAudio(audio, volume, siteId)
            AudioPlayingOption.Disabled           -> return Played
        }
    }

    private suspend fun onLocalPlayAudio(audio: Audio, volume: Float?):SndResult {
        //await audio start
        val collectJob = scope.launch {
            //To file
        }

        //await audio stop
        collectJob.cancel()

        //play
        audioFocusService.request(Sound)
        //TODO sammeln, spielen on stop -> suspend -> finished by tts domain
        localAudioService.playAudio(
            audioSource = AudioSource.Data(audio.data),
            audioOutputOption = params.localOutputOption,
            onFinished = {
                serviceState.value = it
                pipeline.onEvent(PlayedEvent)
            }
        )

        audioFocusService.abandon(Sound)

        return Played
    }

    private suspend fun onRhasspy2HermesHttpPlayAudio(audio: Audio, volume: Float?, siteId: String):SndResult {
        //await audio start
        val collectJob = scope.launch {
            //To file
        }

        //await audio stop
        collectJob.cancel()

        //play
        httpClientConnection.playWav(
            audioSource = AudioSource.Data(play.data),
            onResult = {
                serviceState.value = it.toServiceState()
                pipeline.onEvent(PlayedEvent)
            }
        )

        return Played
    }

    private suspend fun onRhasspy2HermesMQTTPlayAudio(audio: Audio, volume: Float?, siteId: String):SndResult {
        //await audio start
        val collectJob = scope.launch {
            //To file
        }

        //await audio stop
        collectJob.cancel()

        //play
        mqttClientService.playAudioRemote(
            audioSource = AudioSource.Data(play.data),
            siteId = params.mqttSiteId,
            onResult = {
                serviceState.value = it
                pipeline.onEvent(PlayedEvent)
            }
        )

        //await played

        return Played
    }

    override fun stop() {
        scope.cancel()
    }

}