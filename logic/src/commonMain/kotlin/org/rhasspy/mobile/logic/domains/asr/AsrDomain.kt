package org.rhasspy.mobile.logic.domains.asr

import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Record
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.domain.AsrDomainData
import org.rhasspy.mobile.data.service.option.AsrDomainOption
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.AsrResult
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.AsrResult.AsrError
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.AsrResult.AsrTextCaptured
import org.rhasspy.mobile.logic.connections.mqtt.MqttResult
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.logic.connections.user.UserConnectionEvent.StartStopRhasspy
import org.rhasspy.mobile.logic.domains.AudioFileWriter
import org.rhasspy.mobile.logic.domains.IDomainHistory
import org.rhasspy.mobile.logic.domains.mic.MicAudioChunk
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.local.file.IFileStorage
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.pipeline.Source
import org.rhasspy.mobile.logic.pipeline.Source.Local
import org.rhasspy.mobile.logic.pipeline.Source.Rhasspy2HermesMqtt
import org.rhasspy.mobile.logic.pipeline.TranscriptResult
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.*
import org.rhasspy.mobile.logic.pipeline.VadResult.VoiceEnd
import org.rhasspy.mobile.logic.pipeline.VadResult.VoiceEnd.VadTimeout
import org.rhasspy.mobile.logic.pipeline.VadResult.VoiceEnd.VoiceStopped
import org.rhasspy.mobile.logic.pipeline.VadResult.VoiceStart
import org.rhasspy.mobile.platformspecific.timeoutWithDefault

/**
 * AsrDomain tries to detect text within a Flow of MicAudioChunk Events using the defined option
 */
internal interface IAsrDomain : IDomain {

    /**
     * awaits VoiceStart, afterwards audioStream is send to Asr until VoiceStopped or a TranscriptResult is found
     */
    suspend fun awaitTranscript(
        sessionId: String,
        audioStream: Flow<MicAudioChunk>,
        awaitVoiceStart: suspend (audioStream: Flow<MicAudioChunk>) -> VoiceStart,
        awaitVoiceStopped: suspend (audioStream: Flow<MicAudioChunk>) -> VoiceEnd,
    ): TranscriptResult

    val isRecordingState: StateFlow<Boolean>

}

/**
 * AsrDomain tries to detect text within a Flow of MicAudioChunk Events using the defined option
 */
internal class AsrDomain(
    private val params: AsrDomainData,
    private val mqttConnection: IMqttConnection,
    private val rhasspy2HermesConnection: IRhasspy2HermesConnection,
    private val indication: IIndication,
    private val fileStorage: IFileStorage,
    private val audioFocus: IAudioFocus,
    private val userConnection: IUserConnection,
    private val domainHistory: IDomainHistory,
) : IAsrDomain {

    override var isRecordingState = MutableStateFlow(false)

    private val logger = Logger.withTag("AsrDomain")

    private val scope = CoroutineScope(Dispatchers.IO)

    private var audioFileWriter: AudioFileWriter? = null

    /**
     * awaits VoiceStart, afterwards audioStream is send to Asr until VoiceStopped or a TranscriptResult is found
     */
    override suspend fun awaitTranscript(
        sessionId: String,
        audioStream: Flow<MicAudioChunk>,
        awaitVoiceStart: suspend (audioStream: Flow<MicAudioChunk>) -> VoiceStart,
        awaitVoiceStopped: suspend (audioStream: Flow<MicAudioChunk>) -> VoiceEnd,
    ): TranscriptResult {
        logger.d { "awaitTranscript for session $sessionId" }

        isRecordingState.value = true

        indication.onRecording()
        audioFocus.request(Record)

        //wait for voice to start
        awaitVoiceStart(audioStream)

        //await result
        return when (params.option) {
            AsrDomainOption.Rhasspy2HermesHttp ->
                awaitRhasspy2HermesHttpTranscript(
                    audioStream = audioStream,
                    awaitVoiceStopped = awaitVoiceStopped
                )

            AsrDomainOption.Rhasspy2HermesMQTT ->
                awaitRhasspy2HermesMQTTTranscript(
                    sessionId = sessionId,
                    audioStream = audioStream,
                    awaitVoiceStopped = awaitVoiceStopped
                )

            AsrDomainOption.Disabled ->
                TranscriptDisabled(Local)
        }.also {
            audioFocus.abandon(Record)
            isRecordingState.value = false
            domainHistory.addToHistory(it)
        }
    }

    /**
     * collects audioStream into file until VoiceStopped
     * then sends Data to Rhasspy2HermesHttp and returns result
     */
    private suspend fun awaitRhasspy2HermesHttpTranscript(
        audioStream: Flow<MicAudioChunk>,
        awaitVoiceStopped: suspend (audioStream: Flow<MicAudioChunk>) -> VoiceEnd,
    ): TranscriptResult {
        logger.d { "awaitRhasspy2HermesHttpTranscript" }

        val saveDataJob = scope.launch {
            audioStream.collectLatest { chunk ->
                getFileWriter(chunk)
                audioFileWriter?.writeToFile(chunk.data)
            }
        }

        merge(
            userConnection.incomingMessages
                .filterIsInstance<StartStopRhasspy>()
                .map { VoiceStopped(Source.User) },
            flow { emit(awaitVoiceStopped(audioStream)) }
                .filter { it is VoiceStopped }
        ).timeoutWithDefault(
            timeout = params.voiceTimeout,
            default = VadTimeout(Local),
        ).first().also {
            domainHistory.addToHistory(it)
        }

        saveDataJob.cancelAndJoin()
        audioFileWriter?.closeFile()

        return when (val result = rhasspy2HermesConnection.speechToText(fileStorage.speechToTextAudioFile)) {
            is HttpClientResult.HttpClientError -> TranscriptError(source = Source.Rhasspy2HermesHttp)
            is HttpClientResult.Success         -> Transcript(text = result.data, source = Source.Rhasspy2HermesHttp)
        }
    }

    /**
     * sends startListening to MQTT and returns in case it failed
     *
     * collects audioStream into file until VoiceStopped and sends it to mqtt asrAudioSessionFrame
     * result is ignored because it may not be a problem when various frames are dropped
     *
     * sends data until VoiceStopped and then sends stopListening or AsrResult is returned by mqtt
     *
     * in case of stopping by VoiceStopped, AsrResult is awaited with timeout
     */
    private suspend fun awaitRhasspy2HermesMQTTTranscript(
        sessionId: String,
        audioStream: Flow<MicAudioChunk>,
        awaitVoiceStopped: suspend (audioStream: Flow<MicAudioChunk>) -> VoiceEnd,
    ): TranscriptResult {
        logger.d { "awaitRhasspy2HermesMQTTTranscript for session $sessionId" }

        when (
            mqttConnection.startListening(
                sessionId = sessionId,
                isUseSilenceDetection = params.isUseSpeechToTextMqttSilenceDetection,
            )
        ) {
            MqttResult.Error -> return TranscriptError(Rhasspy2HermesMqtt)
            MqttResult.Success -> Unit
        }

        val sendDataJob = scope.launch {
            audioStream.collectLatest { chunk ->
                with(chunk) {
                    mqttConnection.asrAudioSessionFrame(
                        sessionId = sessionId,
                        sampleRate = sampleRate,
                        encoding = encoding,
                        channel = channel,
                        data = data,
                    )
                    getFileWriter(chunk)
                    audioFileWriter?.writeToFile(chunk.data)
                }
            }
        }

        val awaitVoiceStoppedJob = scope.launch {
            merge(
                userConnection.incomingMessages
                    .filterIsInstance<StartStopRhasspy>()
                    .map { VoiceStopped(Source.User) },
                flow { emit(awaitVoiceStopped(audioStream)) }
                    .filter { it is VoiceStopped }
            ).timeoutWithDefault(
                timeout = params.voiceTimeout,
                default = VadTimeout(Local),
            ).first().also {
                domainHistory.addToHistory(it)
            }

            sendDataJob.cancelAndJoin()

            mqttConnection.stopListening(
                sessionId = sessionId,
            )
        }

        return mqttConnection.incomingMessages
            .filterIsInstance<AsrResult>()
            .filter { it.sessionId == sessionId }
            .map {
                when (it) {
                    is AsrTextCaptured -> Transcript(
                        text = it.text ?: return@map TranscriptError(Rhasspy2HermesMqtt),
                        source = Rhasspy2HermesMqtt
                    )

                    is AsrError        -> TranscriptError(Rhasspy2HermesMqtt)
                }
            }
            .timeoutWithDefault(
                timeout = params.mqttResultTimeout,
                default = TranscriptTimeout(Rhasspy2HermesMqtt),
            )
            .first()
            .also {
                sendDataJob.cancelAndJoin()
                awaitVoiceStoppedJob.cancelAndJoin()
                audioFileWriter?.closeFile()
            }
    }

    /**
     * closes file and cancels scope to stop all jobs
     */
    override fun dispose() {
        logger.d { "dispose" }
        audioFileWriter?.closeFile()
        scope.cancel()
    }

    /**
     * creates a file writer if null or returns current
     */
    private fun getFileWriter(chunk: MicAudioChunk): AudioFileWriter {
        logger.d { "getFileWriter $chunk" }
        return audioFileWriter ?: AudioFileWriter(
            path = fileStorage.speechToTextAudioFile,
            channel = chunk.channel.value,
            sampleRate = chunk.sampleRate.value,
            bitRate = chunk.encoding.bitRate,
        ).apply {
            audioFileWriter = this
            openFile()
        }
    }

}