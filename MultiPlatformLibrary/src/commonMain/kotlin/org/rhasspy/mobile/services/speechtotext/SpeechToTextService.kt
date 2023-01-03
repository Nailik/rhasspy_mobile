package org.rhasspy.mobile.services.speechtotext

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.addWavHeader
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.middleware.Action.DialogAction
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.middleware.Source
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.recording.RecordingService
import org.rhasspy.mobile.settings.option.SpeechToTextOption

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
open class SpeechToTextService : IService() {
    private val logger = LogType.SpeechToTextService.logger()

    private val params by inject<SpeechToTextServiceParams>()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success())
    val serviceState = _serviceState.readOnly

    private val httpClientService by inject<HttpClientService>()
    private val mqttClientService by inject<MqttService>()
    private val recordingService by inject<RecordingService>()

    private val serviceMiddleware by inject<IServiceMiddleware>()

    private val _speechToTextAudioData = mutableListOf<Byte>()
    val speechToTextAudioData = _speechToTextAudioData.readOnly

    private val scope = CoroutineScope(Dispatchers.Default)
    private var collector: Job? = null

    override fun onClose() {
        logger.d { "onClose" }
        //nothing to do
        scope.cancel()
        collector?.cancel()
        collector = null
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

        //evaluate result
        when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> {
                val action = httpClientService.speechToText(_speechToTextAudioData.addWavHeader())?.let { text ->
                    DialogAction.AsrTextCaptured(Source.HttpApi, text)
                } ?: run {
                    DialogAction.AsrError(Source.HttpApi)
                }
                serviceMiddleware.action(action)
            }
            SpeechToTextOption.RemoteMQTT -> if (!fromMqtt) mqttClientService.stopListening(sessionId)
            SpeechToTextOption.Disabled -> {}
        }

        //clear data
        _speechToTextAudioData.clear()
    }

    suspend fun startSpeechToText(sessionId: String) {
        logger.d { "startSpeechToText sessionId: $sessionId" }
        //clear data and start recording
        collector?.cancel()
        _speechToTextAudioData.clear()

        if (params.speechToTextOption != SpeechToTextOption.Disabled) {
            //start collection
            collector = scope.launch {
                recordingService.output.collect(::audioFrame)
            }
        }

        when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> {}
            SpeechToTextOption.RemoteMQTT -> mqttClientService.startListening(sessionId)
            SpeechToTextOption.Disabled -> {}
        }
    }

    private suspend fun audioFrame(data: List<Byte>) {
        logger.d { "audioFrame dataSize: ${data.size}" }
        when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> _speechToTextAudioData.addAll(data)
            SpeechToTextOption.RemoteMQTT -> mqttClientService.audioFrame(
                data.toMutableList().addWavHeader()
            )
            SpeechToTextOption.Disabled -> {}//nothing to do
        }
    }

}