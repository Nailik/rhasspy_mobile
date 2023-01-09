package org.rhasspy.mobile.services.speechtotext

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.addWavHeader
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.middleware.Action.DialogAction
import org.rhasspy.mobile.middleware.ServiceMiddleware
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.middleware.Source
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.httpclient.HttpClientResult
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.recording.RecordingService
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.option.SpeechToTextOption

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
open class SpeechToTextService : IService() {
    private val logger = LogType.SpeechToTextService.logger()

    private val params by inject<SpeechToTextServiceParams>()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success)
    val serviceState = _serviceState.readOnly

    private val httpClientService by inject<HttpClientService>()
    private val mqttClientService by inject<MqttService>()
    private val recordingService by inject<RecordingService>()

    private val serviceMiddleware by inject<ServiceMiddleware>()

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
        collector = null
        recordingService.toggleSilenceDetectionEnabled(false)

        //evaluate result
        when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> {
                val result = httpClientService.speechToText(_speechToTextAudioData.addWavHeader())
                _serviceState.value = result.toServiceState()
                val action = when (result) {
                    is HttpClientResult.Error -> DialogAction.AsrError(Source.HttpApi)
                    is HttpClientResult.Success -> DialogAction.AsrTextCaptured(Source.HttpApi, result.data)
                }
                serviceMiddleware.action(action)
            }
            SpeechToTextOption.RemoteMQTT -> if (!fromMqtt) _serviceState.value = mqttClientService.stopListening(sessionId)
            SpeechToTextOption.Disabled -> {}
        }
    }

    suspend fun startSpeechToText(sessionId: String) {
        logger.d { "startSpeechToText sessionId: $sessionId" }

        if(collector?.isActive == true) {
            return
        }

        //clear data and start recording
        collector?.cancel()
        collector = null
        _speechToTextAudioData.clear()

        if (params.speechToTextOption != SpeechToTextOption.Disabled) {
            recordingService.toggleSilenceDetectionEnabled(true)
            //start collection
            collector = scope.launch {
                recordingService.output.collect(::audioFrame)
            }
        }

        _serviceState.value = when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> ServiceState.Success
            SpeechToTextOption.RemoteMQTT -> mqttClientService.startListening(sessionId)
            SpeechToTextOption.Disabled -> ServiceState.Disabled
        }
    }

    private suspend fun audioFrame(data: List<Byte>) {
        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "audioFrame dataSize: ${data.size}" }
        }
        _speechToTextAudioData.addAll(data)

        _serviceState.value = when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> ServiceState.Success
            SpeechToTextOption.RemoteMQTT -> mqttClientService.audioFrame(data.toMutableList().addWavHeader())
            SpeechToTextOption.Disabled -> ServiceState.Disabled
        }
    }

}