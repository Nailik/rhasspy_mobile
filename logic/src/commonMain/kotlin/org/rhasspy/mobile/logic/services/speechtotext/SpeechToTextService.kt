package org.rhasspy.mobile.logic.services.speechtotext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.inject
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Record
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrTextCaptured
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.audiofocus.AudioFocusService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientResult
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.recording.RecordingService
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils.getWavHeader
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.extensions.commonReadWrite
import org.rhasspy.mobile.platformspecific.extensions.commonSize
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
open class SpeechToTextService(
    paramsCreator: SpeechToTextServiceParamsCreator,
) : IService(LogType.SpeechToTextService) {

    private val audioFocusService by inject<AudioFocusService>()
    private val httpClientService by inject<HttpClientService>()
    private val mqttClientService by inject<MqttService>()
    private val recordingService by inject<RecordingService>()

    private val paramsFlow: StateFlow<SpeechToTextServiceParams> = paramsCreator()
    private val params: SpeechToTextServiceParams get() = paramsFlow.value

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success)
    override val serviceState = _serviceState.readOnly

    val speechToTextAudioFile: Path = Path.commonInternalPath(nativeApplication, "SpeechToTextAudio.wav")

    private val scope = CoroutineScope(Dispatchers.Default)
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
    suspend fun endSpeechToText(sessionId: String, fromMqtt: Boolean) {
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

    suspend fun startSpeechToText(sessionId: String, fromMqtt: Boolean) {
        logger.d { "startSpeechToText sessionId: $sessionId fromMqtt $fromMqtt" }

        if (collector?.isActive == true) {
            return
        }
        audioFocusService.request(Record)

        //clear data and start recording
        collector?.cancel()
        collector = null

        speechToTextAudioFile.commonDelete()
        speechToTextAudioFile.commonReadWrite().write(0, ByteArray(0),0,0)

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