package org.rhasspy.mobile.logic.domains.snd

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Sound
import org.rhasspy.mobile.data.domain.SndDomainData
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.PlayResult.PlayFinished
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.domains.AudioFileWriter
import org.rhasspy.mobile.logic.domains.snd.SndAudio.*
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.local.file.IFileStorage
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioPlayer
import org.rhasspy.mobile.logic.pipeline.SndResult
import org.rhasspy.mobile.logic.pipeline.SndResult.Played
import org.rhasspy.mobile.logic.pipeline.TtsResult.Audio
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource

interface ISndDomain : IService {

    //plays until play stop
    suspend fun awaitPlayAudio(audio: Audio): SndResult

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class SndDomain(
    private val params: SndDomainData,
    private val fileStorage: IFileStorage,
    private val audioFocusService: IAudioFocus,
    private val localAudioService: ILocalAudioPlayer,
    private val mqttConnection: IMqttConnection,
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

    override suspend fun awaitPlayAudio(audio: Audio):SndResult {
        return when (params.option) {
            AudioPlayingOption.Local              -> onLocalPlayAudio(audio)
            AudioPlayingOption.Rhasspy2HermesHttp -> onRhasspy2HermesHttpPlayAudio(audio)
            AudioPlayingOption.Rhasspy2HermesMQTT -> onRhasspy2HermesMQTTPlayAudio(audio)
            AudioPlayingOption.Disabled           -> return Played
        }
    }

    private suspend fun onLocalPlayAudio(audio: Audio):SndResult {
        val data = collectAudioToFile(audio)

        //play
        audioFocusService.request(Sound)
        localAudioService.playAudio(
            audioSource = data,
            audioOutputOption = params.localOutputOption,
        )

        audioFocusService.abandon(Sound)

        return Played
    }



    private suspend fun onRhasspy2HermesHttpPlayAudio(audio: Audio):SndResult {
        val data = collectAudioToFile(audio)

        //play
        httpClientConnection.playWav(
            audioSource = data,
        )

        return Played
    }

    private suspend fun onRhasspy2HermesMQTTPlayAudio(audio: Audio):SndResult {
        val data = collectAudioToFile(audio)

        val mqttRequestId = uuid4().toString()

        //play
        mqttConnection.playAudioRemote(
            audioSource = data,
            siteId = params.mqttSiteId,
            id = mqttRequestId,
        )

        //await played TODO timeout
        return mqttConnection.incomingMessages
            .filterIsInstance<PlayFinished>()
            .filter { it.id == mqttRequestId }
            .map {
                Played
            }
            .first()
    }

    private suspend fun collectAudioToFile(audio: Audio): AudioSource {
        //await audio start
        val fileWriter = audio.data
            .filterIsInstance<AudioStartEvent>()
            .map {
                AudioFileWriter(
                    path = fileStorage.playAudioFile,
                    channel = it.channel,
                    sampleRate = it.sampleRate,
                    bitRate = it.bitRate,
                ).apply {
                    openFile()
                }
            }
            .first()

        val collectJob = scope.launch {
            //To file
            audio.data
                .filterIsInstance<AudioChunkEvent>()
                .collect {
                    fileWriter.writeToFile(it.data)
                }
        }

        //await audio stop
        audio.data
            .filterIsInstance<AudioStopEvent>()
            .first()

        collectJob.cancelAndJoin()
        fileWriter.closeFile()

        return  AudioSource.File(fileWriter.path)
    }

    override fun stop() {
        scope.cancel()
    }

}