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
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioPlayer
import org.rhasspy.mobile.logic.pipeline.HandleResult
import org.rhasspy.mobile.logic.pipeline.SndResult
import org.rhasspy.mobile.logic.pipeline.SndResult.*
import org.rhasspy.mobile.logic.pipeline.Source
import org.rhasspy.mobile.logic.pipeline.Source.*
import org.rhasspy.mobile.logic.pipeline.TtsResult.Audio
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.timeoutWithDefault

/**
 * records audio as soon as audioStream has subscribers
 */
interface ISndDomain : IService {

    /**
     * play audio stream from Audio and return SndResult after finished
     */
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
    private val indication: IIndication,
) : ISndDomain {

    private val logger = Logger.withTag("AudioPlayingService")

    override val hasError: ErrorState? = null

    private val scope = CoroutineScope(Dispatchers.IO)

    private var audioFileWriter: AudioFileWriter? = null

    /**
     * play audio stream from Audio and return SndResult after finished
     */
    override suspend fun awaitPlayAudio(audio: Audio): SndResult {
        logger.d { "awaitPlayAudio $audio" }
        indication.onPlayAudio()

        return when (params.option) {
            AudioPlayingOption.Local              -> onLocalPlayAudio(audio)
            AudioPlayingOption.Rhasspy2HermesHttp -> onRhasspy2HermesHttpPlayAudio(audio)
            AudioPlayingOption.Rhasspy2HermesMQTT -> onRhasspy2HermesMQTTPlayAudio(audio)
            AudioPlayingOption.Disabled           -> PlayDisabled
        }
    }

    /**
     * collect chunk stream into file and then plays audio
     */
    private suspend fun onLocalPlayAudio(audio: Audio):SndResult {
        logger.d { "onLocalPlayAudio $audio" }

        val data = collectAudioToFile(audio)

        audioFocusService.request(Sound)

        localAudioService.playAudio(
            audioSource = data,
            audioOutputOption = params.localOutputOption,
        )

        audioFocusService.abandon(Sound)

        return Played(Local)
    }

    /**
     * collect chunk stream into file and then plays audio, doesn't await for end
     */
    private suspend fun onRhasspy2HermesHttpPlayAudio(audio: Audio):SndResult {
        logger.d { "onRhasspy2HermesHttpPlayAudio $audio" }

        val data = collectAudioToFile(audio)

        httpClientConnection.playWav(
            audioSource = data,
        )

        return Played(Rhasspy2HermesHttp)
    }

    /**
     * collect chunk stream into file and then plays audio via mqtt, awaits for play finished
     */
    private suspend fun onRhasspy2HermesMQTTPlayAudio(audio: Audio):SndResult {
        logger.d { "onRhasspy2HermesMQTTPlayAudio $audio" }

        val data = collectAudioToFile(audio)

        val mqttRequestId = uuid4().toString()

        //play
        mqttConnection.playAudioRemote(
            audioSource = data,
            siteId = params.mqttSiteId,
            id = mqttRequestId,
        )

        //await played
        return mqttConnection.incomingMessages
            .filterIsInstance<PlayFinished>()
            .filter { it.id == mqttRequestId }
            .map {
                Played(Rhasspy2HermesMqtt)
            }.timeoutWithDefault(
                timeout = params.rhasspy2HermesMqttTimeout,
                default = NotPlayed(Local),
            )
            .first()
    }

    /**
     * collect chunk stream into file, awaits for AudioStopEvent with timeout
     */
    private suspend fun collectAudioToFile(audio: Audio): AudioSource {
        //await audio start
        val localAudioFileWriter = audio.data
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
        audioFileWriter = localAudioFileWriter

        val collectJob = scope.launch {
            //To file
            audio.data
                .filterIsInstance<AudioChunkEvent>()
                .collect {
                    localAudioFileWriter.writeToFile(it.data)
                }
        }

        //await audio stop
        audio.data
            .filterIsInstance<AudioStopEvent>()
            .timeoutWithDefault(
                timeout = params.audioTimeout,
                default = AudioStopEvent,
            )
            .first()

        collectJob.cancelAndJoin()
        localAudioFileWriter.closeFile()

        return AudioSource.File(localAudioFileWriter.path)
    }

    override fun dispose() {
        scope.cancel()
        audioFileWriter?.closeFile()
    }

}