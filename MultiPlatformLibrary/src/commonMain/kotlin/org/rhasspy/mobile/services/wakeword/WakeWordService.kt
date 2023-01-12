package org.rhasspy.mobile.services.wakeword

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.middleware.Action
import org.rhasspy.mobile.middleware.ServiceMiddleware
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.middleware.Source
import org.rhasspy.mobile.nativeutils.PorcupineWakeWordClient
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.recording.RecordingService
import org.rhasspy.mobile.services.udp.UdpService
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.option.WakeWordOption

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
    private val udpService by inject<UdpService> {
        parametersOf(
            params.wakeWordUdpOutputHost,
            params.wakeWordUdpOutputPort
        )
    }
    private var porcupineWakeWordClient: PorcupineWakeWordClient? = null

    private val recordingService by inject<RecordingService>()

    private val serviceMiddleware by inject<ServiceMiddleware>()

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.readOnly

    private val scope = CoroutineScope(Dispatchers.Default)
    private var recording: Job? = null

    /**
     * starts the service
     */
    init {
        initialize()
    }

    fun initialize() {
        logger.d { "initialization" }
        _serviceState.value = when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {
                _serviceState.value = ServiceState.Loading
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
                error?.also {
                    logger.e(it.exception ?: Throwable()) { "porcupine error" }
                    porcupineWakeWordClient = null
                }
                error?.errorType?.serviceState ?: ServiceState.Success
            }
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
            WakeWordOption.MQTT -> ServiceState.Success
            WakeWordOption.Udp -> ServiceState.Success
            WakeWordOption.Disabled -> ServiceState.Disabled
        }
    }

    private fun onClientError(porcupineError: PorcupineError) {
        _serviceState.value = porcupineError.errorType.serviceState
        logger.e(porcupineError.exception ?: Throwable()) { "porcupineError" }
    }

    fun startDetection() {
        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {
                if (porcupineWakeWordClient == null) {
                    initialize()
                }
                porcupineWakeWordClient?.also {
                    _isRecording.value = true
                    val error = porcupineWakeWordClient?.start()
                    error?.also {
                        logger.e(it.exception ?: Throwable()) { "porcupineError" }
                    }
                    _serviceState.value = error?.errorType?.serviceState ?: ServiceState.Success
                }
            }
            WakeWordOption.MQTT -> {} //nothing will wait for mqtt message
            WakeWordOption.Udp -> {
                _isRecording.value = true
                //collect audio from recorder
                if (recording == null) {
                    recording = scope.launch {
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
            WakeWordOption.MQTT -> {} //nothing will wait for mqtt message
            WakeWordOption.Udp -> scope.launch {
                //needs to be called async else native Audio Recorder stops working
                udpService.streamAudio(data)
            }
            WakeWordOption.Disabled -> {}
        }
    }

    fun stopDetection() {
        logger.d { "stopDetection" }
        _isRecording.value = false
        recording?.cancel()
        recording = null
        porcupineWakeWordClient?.stop()
    }

    private fun onKeywordDetected(hotWord: String) {
        logger.d { "onKeywordDetected $hotWord" }
        serviceMiddleware.action(Action.DialogAction.WakeWordDetected(Source.Local, hotWord))
    }

    override fun onClose() {
        logger.d { "onClose" }
        scope.cancel()
        recording?.cancel()
        recording = null
        porcupineWakeWordClient?.close()
        porcupineWakeWordClient = null
    }

}