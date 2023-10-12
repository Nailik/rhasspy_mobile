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
import org.rhasspy.mobile.logic.pipeline.SndAudio.*
import org.rhasspy.mobile.logic.pipeline.SndResult
import org.rhasspy.mobile.logic.pipeline.SndResult.Played
import org.rhasspy.mobile.logic.pipeline.SndResult.SndError
import org.rhasspy.mobile.logic.pipeline.Source.*
import org.rhasspy.mobile.logic.pipeline.TtsResult.Audio
import org.rhasspy.mobile.logic.pipeline.domain.Reason
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.timeoutWithDefault
import org.rhasspy.mobile.settings.AppSetting

/**
 * records audio as soon as audioStream has subscribers
 */
internal interface ISndDomain : IDomain {

    /**
     * play audio stream from Audio and return SndResult after finished
     */
    suspend fun awaitPlayAudio(audio: Audio): SndResult

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
    private val domainHistory: IDomainHistory,
    private val indication: IIndication,
) : ISndDomain {

    private val logger = Logger.withTag("SndDomain")

    private val scope = CoroutineScope(Dispatchers.IO)

    private var audioFileWriter: AudioFileWriter? = null

    /**
     * play audio stream from Audio and return SndResult after finished
     */
    override suspend fun awaitPlayAudio(audio: Audio): SndResult {
        logger.d { "awaitPlayAudio $audio" }
        //TODO #466 indication.onPlayAudio()

        return when (params.option) {
            SndDomainOption.Local              -> onLocalPlayAudio(audio)
            SndDomainOption.Rhasspy2HermesHttp -> onRhasspy2HermesHttpPlayAudio(audio)
            SndDomainOption.Rhasspy2HermesMQTT -> onRhasspy2HermesMQTTPlayAudio(audio)
            SndDomainOption.Disabled           ->
                SndError(
                    id = null,
                    sessionId = audio.sessionId,
                    reason = Reason.Disabled,
                    source = Local,
                )
        }.also {
            domainHistory.addToHistory(audio, it)
        }
    }

    /**
     * collect chunk stream into file and then plays audio
     */
    private suspend fun onLocalPlayAudio(audio: Audio): SndResult {
        logger.d { "onLocalPlayAudio $audio" }

        val data = withTimeoutOrNull(params.audioTimeout) {
            collectAudioToFile(audio)
        } ?: return SndError(
            id = null,
            sessionId = audio.sessionId,
            reason = Reason.Timeout,
            source = Local,
        )

        audioFocusService.request(Sound)

        if (AppSetting.isAudioOutputEnabled.value) {
            indication.onPlayAudio()

            localAudioService.playAudio(
                audioSource = data,
                volume = AppSetting.volume.value,
                audioOutputOption = params.localOutputOption,
            )
        }

        audioFocusService.abandon(Sound)

        return Played(
            id = null,
            sessionId = audio.sessionId,
            source = Local,
        )
    }

    /**
     * collect chunk stream into file and then plays audio, doesn't await for end
     */
    private suspend fun onRhasspy2HermesHttpPlayAudio(audio: Audio): SndResult {
        logger.d { "onRhasspy2HermesHttpPlayAudio $audio" }

        val data = withTimeoutOrNull(params.audioTimeout) {
            collectAudioToFile(audio)
        } ?: return SndError(
            id = null,
            sessionId = audio.sessionId,
            reason = Reason.Timeout,
            source = Local,
        )

        httpClientConnection.playWav(
            audioSource = data,
        )

        return Played(
            id = null,
            sessionId = audio.sessionId,
            source = Rhasspy2HermesHttp,
        )
    }

    /**
     * collect chunk stream into file and then plays audio via mqtt, awaits for play finished
     */
    private suspend fun onRhasspy2HermesMQTTPlayAudio(audio: Audio): SndResult {
        logger.d { "onRhasspy2HermesMQTTPlayAudio $audio" }

        val data = withTimeoutOrNull(params.audioTimeout) {
            collectAudioToFile(audio)
        } ?: return SndError(
            id = null,
            sessionId = audio.sessionId,
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
                Played(
                    id = mqttRequestId,
                    sessionId = audio.sessionId,
                    source = Rhasspy2HermesMqtt,
                )
            }.timeoutWithDefault(
                timeout = params.rhasspy2HermesMqttTimeout,
                default = SndError(
                    id = mqttRequestId,
                    sessionId = audio.sessionId,
                    reason = Reason.Timeout,
                    source = Local,
                ),
            )
            .first()
    }

    /**
     * collect chunk stream into file, awaits for AudioStopEvent with timeout
     */
    private suspend fun collectAudioToFile(audio: Audio): AudioSource {
        //await audio start
        val audioStartEvent = audio.data
            .filterIsInstance<AudioStartEvent>()
            .first()
            .also {
                domainHistory.addToHistory(audio, it)
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
                domainHistory.addToHistory(audio, it)
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