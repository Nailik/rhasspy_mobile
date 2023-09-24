package org.rhasspy.mobile.logic.domains.asr

import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.domain.AsrDomainData
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.AsrResult
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.AsrResult.AsrError
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.AsrResult.AsrTextCaptured
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.domains.mic.MicAudioChunk
import org.rhasspy.mobile.logic.domains.vad.VadEvent.VoiceStart
import org.rhasspy.mobile.logic.domains.vad.VadEvent.VoiceStopped
import org.rhasspy.mobile.logic.pipeline.TranscriptResult
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.Transcript
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.TranscriptError

/**
 * AsrDomain checks tries to detect text within a Flow of MicAudioChunk Events
 */
interface IAsrDomain : IService {

    /**
     * collect audioStream as soon as VoiceStart until VoiceStopped if present
     */
    suspend fun awaitTranscript(
        sessionId: String,
        audioStream: Flow<MicAudioChunk>,
        awaitVoiceStart: suspend () -> VoiceStart,
        awaitVoiceStopped: suspend () -> VoiceStopped,
    ): TranscriptResult

}



//TODO incoming events from mqtt?
//TODO stop from mqtt -> necessary do not call  mqttClientService.stopListening
/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class AsrDomain(
    private val params: AsrDomainData,
    private val asrFileWriter: AsrFileWriter,
    private val mqttConnection: IMqttConnection,
    private val rhasspy2HermesConnection: IRhasspy2HermesConnection,
) : IAsrDomain {

    private val logger = Logger.withTag("SpeechToTextService")

    override val serviceState = MutableStateFlow<ServiceState>(Loading)

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        serviceState.value = when (params.option) {
            SpeechToTextOption.Rhasspy2HermesHttp -> Success
            SpeechToTextOption.Rhasspy2HermesMQTT -> Success
            SpeechToTextOption.Disabled           -> Disabled
        }
    }

    override suspend fun awaitTranscript(
        sessionId: String,
        audioStream: Flow<MicAudioChunk>,
        awaitVoiceStart: suspend () -> VoiceStart,
        awaitVoiceStopped: suspend () -> VoiceStopped,
    ): TranscriptResult {
        //wait for voice to start
        awaitVoiceStart()

        //open the file asr audio file to write into
        asrFileWriter.openFile()

        //await result
        return when (params.option) {
            SpeechToTextOption.Rhasspy2HermesHttp ->
                awaitRhasspy2HermesHttpTranscript(
                    audioStream = audioStream,
                    awaitVoiceStopped = awaitVoiceStopped
                )
            SpeechToTextOption.Rhasspy2HermesMQTT ->
                awaitRhasspy2HermesMQTTTranscript(
                    sessionId = sessionId,
                    audioStream = audioStream,
                    awaitVoiceStopped = awaitVoiceStopped
                )
            SpeechToTextOption.Disabled           -> TranscriptError
        }.also {
            //close asr audio file
            asrFileWriter.closeFile()
        }
    }

    private suspend fun awaitRhasspy2HermesHttpTranscript(
        audioStream: Flow<MicAudioChunk>,
        awaitVoiceStopped: suspend () -> VoiceStopped,
    ): TranscriptResult {

        val saveDataJob = scope.launch {
            audioStream.collectLatest { chunk ->
                asrFileWriter.writeToFile(chunk)
            }
        }

        //TODO timeout
        awaitVoiceStopped()

        saveDataJob.cancel()

        val result = rhasspy2HermesConnection.speechToText(asrFileWriter.filePath)
        serviceState.value = result.toServiceState()

        return when (result) {
            is HttpClientResult.HttpClientError -> TranscriptError
            is HttpClientResult.Success         -> Transcript(text = result.data)
        }
    }

    private suspend fun awaitRhasspy2HermesMQTTTranscript(
        sessionId: String,
        audioStream: Flow<MicAudioChunk>,
        awaitVoiceStopped: suspend () -> VoiceStopped,
    ): TranscriptResult {

        mqttConnection.startListening(
            sessionId = sessionId,
            isUseSilenceDetection = params.isUseSpeechToTextMqttSilenceDetection,
            onResult = { serviceState.value = it }
        )

        val sendDataJob = scope.launch {
            audioStream.collectLatest { chunk ->
                with(chunk) {
                    asrFileWriter.writeToFile(chunk)
                    mqttConnection.asrAudioSessionFrame(
                        sessionId = sessionId,
                        sampleRate = sampleRate,
                        encoding = encoding,
                        channel = channel,
                        data = data,
                        onResult = { serviceState.value = it }
                    )
                }
            }
        }

        val awaitVoiceStoppedJob = scope.launch {
            awaitVoiceStopped()
            //TODO timeout
            sendDataJob.cancel()
        }

        return mqttConnection.incomingMessages
            .filterIsInstance<AsrResult>()
            .filter { it.sessionId == sessionId }
            .map {
                when (it) {
                    is AsrTextCaptured -> Transcript(it.text ?: return@map TranscriptError)
                    is AsrError -> TranscriptError
                }
            }
            .first()
            //TODO timeout
            .also {
                sendDataJob.cancel()
                awaitVoiceStoppedJob.cancel()
            }
    }

    override fun stop() {
        asrFileWriter.closeFile()
        scope.cancel()
    }

}