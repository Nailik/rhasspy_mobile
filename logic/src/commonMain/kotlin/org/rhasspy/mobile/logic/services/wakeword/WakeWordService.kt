package org.rhasspy.mobile.logic.services.wakeword

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.data.porcupine.PorcupineError
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.WakeWordDetected
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.recording.RecordingService
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.porcupine.PorcupineWakeWordClient
import org.rhasspy.mobile.platformspecific.readOnly

/**
 * hot word services listens for hot word, evaluates configuration settings but no states
 *
 * calls stateMachineService when hot word was detected
 */
class WakeWordService(
    paramsCreator: WakeWordServiceParamsCreator,
) : IService(LogType.WakeWordService) {

    private val recordingService by inject<RecordingService>()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    override val serviceState = _serviceState.readOnly

    private val paramsFlow: StateFlow<WakeWordServiceParams> = paramsCreator()
    private val params: WakeWordServiceParams get() = paramsFlow.value

    private var udpConnection: UdpConnection? = null
    private var porcupineWakeWordClient: PorcupineWakeWordClient? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.readOnly

    private val scope = CoroutineScope(Dispatchers.Default)
    private var recording: Job? = null

    /**
     * starts the service
     */
    init {
        scope.launch {
            paramsFlow.collect {
                stop()
                start()
            }
        }
    }

    private fun start() {
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
                checkPorcupineInitialized()
            }
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
            WakeWordOption.MQTT -> {
                _serviceState.value = ServiceState.Loading

                udpConnection = UdpConnection(
                    params.wakeWordUdpOutputHost,
                    params.wakeWordUdpOutputPort
                )

                checkUdpConnection()
            }

            WakeWordOption.Udp -> ServiceState.Success
            WakeWordOption.Disabled -> ServiceState.Disabled
        }
    }

    private fun stop() {
        logger.d { "onClose" }
        scope.cancel()
        recording?.cancel()
        recording = null
        porcupineWakeWordClient?.close()
        porcupineWakeWordClient = null
        udpConnection?.close()
        udpConnection = null
    }

    private fun onClientError(porcupineError: PorcupineError) {
        _serviceState.value = porcupineError.errorType.serviceState
        logger.e(porcupineError.exception ?: Throwable()) { "porcupineError" }
    }

    fun startDetection() {
        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {

                _serviceState.value = checkPorcupineInitialized()

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

    private suspend fun hotWordAudioFrame(data: ByteArray) {
        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "hotWordAudioFrame dataSize: ${data.size}" }
        }
        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {}
            WakeWordOption.MQTT -> {} //nothing will wait for mqtt message
            WakeWordOption.Udp -> {

                _serviceState.value = checkUdpConnection()

                scope.launch {
                    //needs to be called async else native Audio Recorder stops working
                    udpConnection?.streamAudio(data)
                }
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
        serviceMiddleware.action(WakeWordDetected(Source.Local, hotWord))
    }

    private fun checkUdpConnection(): ServiceState {
        return if (udpConnection?.isConnected == false) {
            udpConnection?.connect()?.let {
                ServiceState.Exception(it)
            } ?: ServiceState.Success
        } else ServiceState.Exception()
    }

    private fun checkPorcupineInitialized(): ServiceState {
        return if (porcupineWakeWordClient?.isInitialized == false) {
            val error = porcupineWakeWordClient?.initialize()
            error?.also {
                logger.e(it.exception ?: Throwable()) { "porcupine error" }
                porcupineWakeWordClient = null
            }
            error?.errorType?.serviceState ?: ServiceState.Success
        } else ServiceState.Exception()
    }

}