package org.rhasspy.mobile.services.wakeword

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.middleware.Action
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.middleware.Source
import org.rhasspy.mobile.nativeutils.PorcupineWakeWordClient
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.recording.RecordingService
import org.rhasspy.mobile.services.udp.UdpService
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.option.WakeWordOption

//TODO logging
/**
 * hot word services listens for hot word, evaluates configuration settings but no states
 *
 * calls stateMachineService when hot word was detected
 */
class WakeWordService : IService() {
    private val logger = LogType.WakeWordService.logger()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    val serviceState = _serviceState.readOnly

    private val params by inject<WakeWordServiceParams>()
    private val udpService by inject<UdpService>()
    private val mqttService by inject<MqttService>()
    private var porcupineWakeWordClient: PorcupineWakeWordClient? = null

    private val recordingService = get<RecordingService>() //TODO why does "inject" crash here

    private val serviceMiddleware by inject<IServiceMiddleware>()

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.readOnly

    private var recording: Job? = null

    /**
     * starts the service
     */
    init {
        logger.d { "initialization" }
        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {
                //when porcupine is used for hotWord then start local service
                porcupineWakeWordClient = PorcupineWakeWordClient(
                    params.wakeWordPorcupineAccessToken,
                    params.wakeWordPorcupineKeywordDefaultOptions,
                    params.wakeWordPorcupineKeywordCustomOptions,
                    params.wakeWordPorcupineLanguage,
                    ::onKeywordDetected,
                    ::onClientError
                )
                val error = porcupineWakeWordClient?.initialize()
                if (error?.exception != null) {
                    logger.e(error.exception) { "porcupine error $error" }
                } else if (error != null) {
                    logger.e { "porcupine error $error" }
                }
            }
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
            WakeWordOption.MQTT -> {} //nothing to do
            WakeWordOption.Udp -> {} //nothing to do
            WakeWordOption.Disabled -> {}
        }
    }

    private fun onClientError(porcupineError: PorcupineError) {
        if (porcupineError.exception != null) {
            logger.e(porcupineError.exception) { "porcupineError $porcupineError" }
        } else {
            logger.e { "porcupineError $porcupineError" }
        }
    }

    fun startDetection() {
        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {
                _isRecording.value = true
                val error = porcupineWakeWordClient?.start()
                if (error?.exception != null) {
                    logger.e(error.exception) { "porcupineError $error" }
                } else if (error != null) {
                    logger.e { "porcupineError $error" }
                }
            }
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
            WakeWordOption.MQTT,
            WakeWordOption.Udp -> {
                _isRecording.value = true
                //collect audio from recorder
                if (recording == null) {
                    recording = CoroutineScope(Dispatchers.Default).launch {
                        recordingService.output.collect(::hotWordAudioFrame)
                    }
                }
            }
            WakeWordOption.Disabled -> {}
        }
    }

    private suspend fun hotWordAudioFrame(data: List<Byte>) {
        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "hotWordAudioFrame dataSize: ${data.size}" }
        }
        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {}
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
            WakeWordOption.MQTT -> mqttService.audioFrame(data)
            WakeWordOption.Udp -> udpService.streamAudio(data)
            WakeWordOption.Disabled -> {}
        }
    }

    fun stopDetection() {
        logger.d { "stopDetection" }
        _isRecording.value = false
        recording?.cancel()
        recording = null
        recordingService.close()
        porcupineWakeWordClient?.stop()
    }

    private fun onKeywordDetected(hotWord: String) {
        logger.d { "onKeywordDetected $hotWord" }
        serviceMiddleware.action(Action.DialogAction.WakeWordDetected(Source.Local, hotWord))
    }

    override fun onClose() {
        logger.d { "onClose" }
        stopDetection()
        porcupineWakeWordClient?.close()
    }

}