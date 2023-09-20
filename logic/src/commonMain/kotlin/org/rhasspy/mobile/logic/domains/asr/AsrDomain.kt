package org.rhasspy.mobile.logic.domains.asr

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okio.FileHandle
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.local.file.IFileStorage
import org.rhasspy.mobile.logic.pipeline.IPipeline
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.AsrDomainEvent.TranscriptErrorEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.AsrDomainEvent.TranscriptEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.AudioDomainEvent.*
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils.getWavHeader
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonReadWrite
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IAsrDomain : IService {

    override val serviceState: StateFlow<ServiceState>

    fun onAudioStart(audioStart: AudioStartEvent, sessionId: String)

    fun onAudioChunk(chunk: AudioChunkEvent, sessionId: String)

    fun onAudioStop(audioStop: AudioStopEvent, sessionId: String)

}

//TODO incoming events from mqtt?
//TODO stop from mqtt -> necessary do not call  mqttClientService.stopListening
/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class AsrDomain(
    private val pipeline: IPipeline,
    private val fileStorage: IFileStorage,
    private val mqttConnection: IMqttConnection,
    private val rhasspy2HermesConnection: IRhasspy2HermesConnection,
) : IAsrDomain {

    private val logger = Logger.withTag("SpeechToTextService")

    override val serviceState = MutableStateFlow<ServiceState>(Pending)
    private val params get() = ConfigurationSetting.asrDomainData.value

    private val scope = CoroutineScope(Dispatchers.IO)

    private var fileHandle: FileHandle? = null

    private var sampleRate = AudioFormatSampleRateType.default
    private var encoding = AudioFormatEncodingType.default
    private var channel = AudioFormatChannelType.default

    init {
        scope.launch {
            ConfigurationSetting.asrDomainData.data.collectLatest {
                closeFile()
                initialize()
            }
        }
    }

    private fun initialize() {
        serviceState.value = when (params.option) {
            SpeechToTextOption.Rhasspy2HermesHttp -> Success
            SpeechToTextOption.Rhasspy2HermesMQTT -> Success
            SpeechToTextOption.Disabled           -> Disabled
        }
    }

    /**
     * starts when first audio chunk is received after stop event
     */
    override fun onAudioStart(audioStart: AudioStartEvent, sessionId: String) {
        sampleRate = audioStart.sampleRate
        encoding = audioStart.encoding
        channel = audioStart.channel

        closeFile()
        fileStorage.speechToTextAudioFile.commonDelete()
        fileHandle = fileStorage.speechToTextAudioFile.commonReadWrite()

        serviceState.value = when (params.option) {
            SpeechToTextOption.Rhasspy2HermesHttp -> Success
            SpeechToTextOption.Rhasspy2HermesMQTT -> {
                mqttConnection.startListening(
                    sessionId = sessionId,
                    isUseSilenceDetection = params.isUseSpeechToTextMqttSilenceDetection,
                    onResult = { serviceState.value = it }
                )
                Loading
            }

            SpeechToTextOption.Disabled           -> Disabled
        }
    }

    override fun onAudioChunk(chunk: AudioChunkEvent, sessionId: String) {
        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "audioFrame dataSize: ${chunk.data.size}" }
        }

        with(chunk) {
            serviceState.value = when (params.option) {
                SpeechToTextOption.Rhasspy2HermesHttp -> Success
                SpeechToTextOption.Rhasspy2HermesMQTT -> {
                    mqttConnection.asrAudioSessionFrame(
                        sessionId = sessionId,
                        sampleRate = sampleRate,
                        encoding = encoding,
                        channel = channel,
                        data = data,
                        onResult = { serviceState.value = it }
                    )
                    Loading
                }

                SpeechToTextOption.Disabled           -> Disabled
            }

            //write async after data was send
            fileHandle?.write(
                fileOffset = fileHandle?.size() ?: 0,
                array = data,
                arrayOffset = 0,
                byteCount = data.size
            )
        }
    }


    /**
     * Speech to Text (Wav Data)
     * used to translate last spoken
     *
     * HTTP:
     * - calls service to translate speech to text, then handles the intent if dialogue manager is set to local
     *
     * RemoteMQTT
     * - audio was already send to mqtt while recording in audioFrame
     *
     * fromMqtt is used to check if silence was detected by remote mqtt device
     */
    override fun onAudioStop(audioStop: AudioStopEvent, sessionId: String) {
        //add wav header to file
        val header = getWavHeader(
            sampleRate = sampleRate,
            encoding = encoding,
            channel = channel,
            audioSize = fileHandle?.size() ?: 0
        )

        fileHandle?.write(0, header, 0, header.size)
        fileHandle?.flush()
        fileHandle?.close()
        fileHandle = null

        //evaluate result
        serviceState.value = when (params.option) {
            SpeechToTextOption.Rhasspy2HermesHttp -> {
                rhasspy2HermesConnection.speechToText(fileStorage.speechToTextAudioFile) { result ->
                    result.toServiceState()
                    val event = when (result) {
                        is HttpClientResult.HttpClientError -> TranscriptErrorEvent
                        is HttpClientResult.Success         -> TranscriptEvent(result.data)
                    }
                    pipeline.onEvent(event)
                }
                Loading
            }

            SpeechToTextOption.Rhasspy2HermesMQTT -> {
                mqttConnection.stopListening(
                    sessionId = sessionId,
                    onResult = { serviceState.value = it }
                )
                Loading
            }

            SpeechToTextOption.Disabled           -> Disabled
        }
    }

    private fun closeFile() {
        fileHandle?.apply {
            flush()
            close()
        }
        fileHandle = null
    }

}