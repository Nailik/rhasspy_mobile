package org.rhasspy.mobile.logic.services.speechtotext

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okio.Path
import org.koin.core.component.inject
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Record
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrTextCaptured
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.audiofocus.IAudioFocusService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientResult
import org.rhasspy.mobile.logic.services.httpclient.IHttpClientService
import org.rhasspy.mobile.logic.services.mqtt.IMqttService
import org.rhasspy.mobile.logic.services.recording.IRecordingService
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils.getWavHeader
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.extensions.commonReadWrite
import org.rhasspy.mobile.platformspecific.extensions.commonSize
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting

interface ISpeechToTextService : IService {

    override val serviceState: StateFlow<ServiceState>

    val speechToTextAudioFile: Path

    suspend fun endSpeechToText(sessionId: String, fromMqtt: Boolean)
    suspend fun startSpeechToText(sessionId: String, fromMqtt: Boolean)

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class SpeechToTextService(
    paramsCreator: SpeechToTextServiceParamsCreator,
) : ISpeechToTextService {

    override val logger = LogType.SpeechToTextService.logger()

    private val audioFocusService by inject<IAudioFocusService>()
    private val httpClientService by inject<IHttpClientService>()
    private val mqttClientService by inject<IMqttService>()
    private val recordingService by inject<IRecordingService>()
    private val nativeApplication by inject<INativeApplication>()
    private val serviceMiddleware by inject<IServiceMiddleware>()

    private val paramsFlow: StateFlow<SpeechToTextServiceParams> = paramsCreator()
    private val params: SpeechToTextServiceParams get() = paramsFlow.value

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success)
    override val serviceState = _serviceState.readOnly

    override val speechToTextAudioFile: Path = Path.commonInternalPath(nativeApplication, "SpeechToTextAudio.wav")

    private val scope = CoroutineScope(Dispatchers.IO)
    private var collector: Job? = null

    init {
        scope.launch {
            paramsFlow.collect {
                collector?.cancel()
                collector = null
            }
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
    override suspend fun endSpeechToText(sessionId: String, fromMqtt: Boolean) {
        logger.d { "endSpeechToText sessionId: $sessionId fromMqtt $fromMqtt" }

        //stop collection
        collector?.cancel()
        collector = null

        audioFocusService.abandon(Record)

        //add wav header to file
        val header = getWavHeader(
            AppSetting.audioRecorderChannel.value,
            AppSetting.audioRecorderSampleRate.value,
            AppSetting.audioRecorderEncoding.value,
            speechToTextAudioFile.commonSize() ?: 0
        )

        speechToTextAudioFile.commonReadWrite().write(0, header, 0, header.size)

        recordingService.toggleSilenceDetectionEnabled(false)

        //evaluate result
        when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> {
                val result = httpClientService.speechToText(speechToTextAudioFile)
                _serviceState.value = result.toServiceState()
                val action = when (result) {
                    is HttpClientResult.Error -> AsrError(Source.HttpApi)
                    is HttpClientResult.Success -> AsrTextCaptured(Source.HttpApi, result.data)
                }
                serviceMiddleware.action(action)
            }

            SpeechToTextOption.RemoteMQTT -> if (!fromMqtt) _serviceState.value = mqttClientService.stopListening(sessionId)
            SpeechToTextOption.Disabled -> {}
        }
    }

    override suspend fun startSpeechToText(sessionId: String, fromMqtt: Boolean) {
        logger.d { "startSpeechToText sessionId: $sessionId fromMqtt $fromMqtt" }

        if (collector?.isActive == true) {
            return
        }
        audioFocusService.request(Record)

        //clear data and start recording
        collector?.cancel()
        collector = null

        speechToTextAudioFile.commonDelete()
        speechToTextAudioFile.commonReadWrite().write(0, ByteArray(0), 0, 0)

        if (params.speechToTextOption != SpeechToTextOption.Disabled) {
            recordingService.toggleSilenceDetectionEnabled(true)
        }
        //start collection
        collector = scope.launch {
            recordingService.output.collect {
                audioFrame(sessionId, it)
            }
        }

        _serviceState.value = when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> ServiceState.Success
            SpeechToTextOption.RemoteMQTT -> if (!fromMqtt) mqttClientService.startListening(sessionId) else ServiceState.Success
            SpeechToTextOption.Disabled -> ServiceState.Disabled
        }
    }

    private suspend fun audioFrame(sessionId: String, data: ByteArray) {
        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "audioFrame dataSize: ${data.size}" }
        }

        _serviceState.value = when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> ServiceState.Success
            SpeechToTextOption.RemoteMQTT -> mqttClientService.asrAudioFrame(sessionId, data)
            SpeechToTextOption.Disabled -> ServiceState.Disabled
        }

        scope.launch {
            //write async after data was send
            speechToTextAudioFile.commonReadWrite().write(
                fileOffset = speechToTextAudioFile.commonSize() ?: 0,
                array = data,
                arrayOffset = 0,
                byteCount = data.size
            )
        }
    }

}