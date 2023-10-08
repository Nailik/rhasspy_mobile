package org.rhasspy.mobile.logic.domains.snd

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Sound
import org.rhasspy.mobile.data.domain.SndDomainData
import org.rhasspy.mobile.data.service.option.SndDomainOption
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.PlayResult.PlayFinished
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.domains.AudioFileWriter
import org.rhasspy.mobile.logic.domains.IDomainHistory
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.local.file.IFileStorage
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioPlayer
import org.rhasspy.mobile.logic.pipeline.Reason
import org.rhasspy.mobile.logic.pipeline.SndAudio.*
import org.rhasspy.mobile.logic.pipeline.SndResult
import org.rhasspy.mobile.logic.pipeline.SndResult.Played
import org.rhasspy.mobile.logic.pipeline.SndResult.SndError
import org.rhasspy.mobile.logic.pipeline.Source.*
import org.rhasspy.mobile.logic.pipeline.TtsResult.Audio
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.timeoutWithDefault

/**
 * records audio as soon as audioStream has subscribers
 */
internal interface ISndDomain : IDomain {

    /**
     * play audio stream from Audio and return SndResult after finished
     */
    suspend fun awaitPlayAudio(id: String, audio: Audio): SndResult

}

/**
 * records audio as soon as audioStream has subscribers
 */
internal class SndDomain(
    private val params: SndDomainData,
    private val fileStorage: IFileStorage,
    private val audioFocusService: IAudioFocus,
    private val localAudioService: ILocalAudioPlayer,
    private val mqttConnection: IMqttConnection,
    private val httpClientConnection: IRhasspy2HermesConnection,
    private val indication: IIndication,
    private val domainHistory: IDomainHistory,
) : ISndDomain {

    private val logger = Logger.withTag("SndDomain")

    private val scope = CoroutineScope(Dispatchers.IO)

    private var audioFileWriter: AudioFileWriter? = null

    /**
     * play audio stream from Audio and return SndResult after finished
     */
    override suspend fun awaitPlayAudio(id: String, audio: Audio): SndResult {
        logger.d { "awaitPlayAudio $audio" }
        //TODO #466 indication.onPlayAudio()

        return when (params.option) {
            SndDomainOption.Local              -> onLocalPlayAudio(id, audio)
            SndDomainOption.Rhasspy2HermesHttp -> onRhasspy2HermesHttpPlayAudio(id, audio)
            SndDomainOption.Rhasspy2HermesMQTT -> onRhasspy2HermesMQTTPlayAudio(id, audio)
            SndDomainOption.Disabled           ->
                SndError(
                    reason = Reason.Disabled,
                    source = Local,
                )
        }.also {
            domainHistory.addToHistory(id, it)
        }
    }

    /**
     * collect chunk stream into file and then plays audio
     */
    private suspend fun onLocalPlayAudio(id: String, audio: Audio): SndResult {
        logger.d { "onLocalPlayAudio $audio" }

        val data = withTimeoutOrNull(params.audioTimeout) {
            collectAudioToFile(id, audio)
        } ?: return SndError(
            reason = Reason.Timeout,
            source = Local,
        )

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
    private suspend fun onRhasspy2HermesHttpPlayAudio(id: String, audio: Audio): SndResult {
        logger.d { "onRhasspy2HermesHttpPlayAudio $audio" }

        val data = withTimeoutOrNull(params.audioTimeout) {
            collectAudioToFile(id, audio)
        } ?: return SndError(
            reason = Reason.Timeout,
            source = Local,
        )

        httpClientConnection.playWav(
            audioSource = data,
        )

        return Played(Rhasspy2HermesHttp)
    }

    /**
     * collect chunk stream into file and then plays audio via mqtt, awaits for play finished
     */
    private suspend fun onRhasspy2HermesMQTTPlayAudio(id: String, audio: Audio): SndResult {
        logger.d { "onRhasspy2HermesMQTTPlayAudio $audio" }

        val data = withTimeoutOrNull(params.audioTimeout) {
            collectAudioToFile(id, audio)
        } ?: return SndError(
            reason = Reason.Timeout,
            source = Local,
        )

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
                default = SndError(
                    reason = Reason.Timeout,
                    source = Local,
                ),
            )
            .first()
    }

    /**
     * collect chunk stream into file, awaits for AudioStopEvent with timeout
     */
    private suspend fun collectAudioToFile(id: String, audio: Audio): AudioSource {
        //await audio start
        val audioStartEvent = audio.data
            .filterIsInstance<AudioStartEvent>()
            .first()
            .also {
                domainHistory.addToHistory(id, it)
            }

        val localAudioFileWriter = AudioFileWriter(
            path = fileStorage.playAudioFile,
            channel = audioStartEvent.channel,
            sampleRate = audioStartEvent.sampleRate,
            bitRate = audioStartEvent.bitRate,
        ).apply {
            openFile()
        }

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
            .first()
            .also {
                domainHistory.addToHistory(id, it)
            }

        collectJob.cancelAndJoin()
        localAudioFileWriter.closeFile()

        return AudioSource.File(localAudioFileWriter.path)
    }

    override fun dispose() {
        scope.cancel()
        audioFileWriter?.closeFile()
    }

}