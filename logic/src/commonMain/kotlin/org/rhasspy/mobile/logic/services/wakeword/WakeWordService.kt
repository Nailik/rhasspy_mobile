package org.rhasspy.mobile.logic.services.wakeword

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.WakeWordDetected
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils.appendWavHeader
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.porcupine.PorcupineWakeWordClient
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.resampler.Resampler
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.AppSetting
import kotlin.Exception

interface IWakeWordService : IService {

    override val serviceState: StateFlow<ServiceState>

    val isRecording: StateFlow<Boolean>

    fun startDetection()
    fun stopDetection()

}

/**
 * hot word services listens for hot word, evaluates configuration settings but no states
 *
 * calls stateMachineService when hot word was detected
 */
internal class WakeWordService(
    paramsCreator: WakeWordServiceParamsCreator,
    private val audioRecorder: IAudioRecorder
) : IWakeWordService {

    override val logger = LogType.WakeWordService.logger()

    private val _serviceState = MutableStateFlow<ServiceState>(Pending)
    override val serviceState = _serviceState.readOnly

    private val serviceMiddleware by inject<IServiceMiddleware>()

    private val paramsFlow: StateFlow<WakeWordServiceParams> = paramsCreator()
    private val params: WakeWordServiceParams get() = paramsFlow.value

    private var udpConnection: UdpConnection? = null
    private var porcupineWakeWordClient: PorcupineWakeWordClient? = null

    private val _isRecording = MutableStateFlow(false)
    override val isRecording = _isRecording.readOnly

    private var isDetectionRunning = false

    private val scope = CoroutineScope(Dispatchers.IO)
    private var recording: Job? = null

    private var initializedParams: WakeWordServiceParams? = null


    private var resampler: Resampler? = null

    private fun getResampler(): Resampler {
        return resampler ?: Resampler(
            inputChannelType = params.audioRecorderChannelType,
            inputEncodingType = params.audioRecorderEncodingType,
            inputSampleRateType = params.audioRecorderSampleRateType,
            outputChannelType = params.audioOutputChannelType,
            outputEncodingType = params.audioOutputEncodingType,
            outputSampleRateType = params.audioOutputSampleRateType,
        ).also {
            resampler = it
        }
    }

    /**
     * starts the service
     */
    init {
        scope.launch {
            paramsFlow.collect {
                resampler?.dispose()
                resampler = null

                updateState()
                if (it != initializedParams?.copy(isEnabled = it.isEnabled)) {
                    stop()
                    disposeOld()
                }
                if (isDetectionRunning && it.isEnabled) {
                    startDetection()
                } else {
                    stop()
                }
            }
        }
    }

    private fun updateState() {
        _serviceState.value = when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> Pending
            WakeWordOption.MQTT      -> Success
            WakeWordOption.Udp       -> Pending
            WakeWordOption.Disabled  -> Disabled
        }
    }

    override fun startDetection() {
        logger.d { "startDetection $isDetectionRunning" }
        isDetectionRunning = true

        if (!params.isEnabled) return

        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> startPorcupine()
            WakeWordOption.Udp       -> startUdp()
            else                     -> Unit
        }
    }

    override fun stopDetection() {
        logger.d { "stopDetection $isDetectionRunning" }
        if (!isDetectionRunning) return
        stop()
        isDetectionRunning = false
    }

    private fun stop() {
        logger.d { "stop" }
        _isRecording.value = false
        recording?.cancel()
        recording = null
        try {
            porcupineWakeWordClient?.stop()
        } catch (e: Exception) {
            logger.e(e) { "porcupineWakeWordClient stop" }
        }
        porcupineWakeWordClient = null
    }


    private fun startPorcupine() {
        initialize()

        _serviceState.value = porcupineWakeWordClient?.let { client ->
            _isRecording.value = true

            val error = client.start()
            error?.let {
                logger.e(it) { "porcupineError" }
                ServiceState.Exception(it)
            } ?: Success
        } ?: Error(MR.strings.notInitialized.stable)
    }


    private fun startUdp() {
        initialize()

        _serviceState.value = udpConnection?.let { client ->
            _isRecording.value = true

            val error = client.connect()
            error?.let {
                logger.e(it) { "porcupineError" }
                ServiceState.Exception(it)
            } ?: Success
        } ?: Error(MR.strings.notInitialized.stable)

        if (_serviceState.value == Success) {
            if (recording == null) {
                audioRecorder.startRecording(
                    audioRecorderChannelType = params.audioRecorderChannelType,
                    audioRecorderEncodingType = params.audioRecorderEncodingType,
                    audioRecorderSampleRateType = params.audioRecorderSampleRateType,
                )

                recording = scope.launch {
                    audioRecorder.output.collect(::hotWordAudioFrame)
                }
            }
        }
    }

    private fun initialize() {
        initializedParams?.also {
            //ignore enabled flag when comparing
            if (it == params.copy(isEnabled = it.isEnabled)) {
                when (params.wakeWordOption) {
                    WakeWordOption.Porcupine -> if (porcupineWakeWordClient != null) return
                    WakeWordOption.MQTT      -> return
                    WakeWordOption.Udp       -> if (udpConnection != null) return
                    WakeWordOption.Disabled  -> return
                }
            }
        }

        //params changed dispose old
        disposeOld()

        initializedParams = params

        logger.d { "initialization" }
        _serviceState.value = when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {
                _serviceState.value = Loading
                //when porcupine is used for hotWord then start local service
                porcupineWakeWordClient = PorcupineWakeWordClient(
                    params.audioRecorderSampleRateType,
                    params.audioRecorderChannelType,
                    params.wakeWordPorcupineAccessToken,
                    params.wakeWordPorcupineKeywordDefaultOptions,
                    params.wakeWordPorcupineKeywordCustomOptions,
                    params.wakeWordPorcupineLanguage,
                    ::onKeywordDetected,
                    ::onClientError
                )

                Success
            }
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
            WakeWordOption.MQTT      -> Success
            WakeWordOption.Udp       -> {
                _serviceState.value = Loading

                udpConnection = UdpConnection(
                    params.wakeWordUdpOutputHost,
                    params.wakeWordUdpOutputPort
                )

                Success
            }

            WakeWordOption.Disabled -> Disabled
        }
    }

    private fun disposeOld() {
        logger.d { "stop" }
        recording?.cancel()
        recording = null
        try {
            porcupineWakeWordClient?.close()
        } catch (e: Exception) {
            logger.e(e) { "porcupineWakeWordClient stop" }
        }
        udpConnection?.close()
        udpConnection = null
        porcupineWakeWordClient?.close()
        porcupineWakeWordClient = null
    }


    private fun onClientError(exception: Exception) {
        _serviceState.value = ServiceState.Exception(exception)
        logger.e(exception) { "porcupineError" }
    }


    private suspend fun hotWordAudioFrame(data: ByteArray) {
        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "hotWordAudioFrame dataSize: ${data.size}" }
        }

        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> Unit
            WakeWordOption.MQTT      -> Unit //nothing will wait for mqtt message
            WakeWordOption.Udp       -> udpConnection?.streamAudio(
                data = getResampler()
                    .resample(data)
                    .appendWavHeader(
                        audioRecorderChannelType = params.audioOutputChannelType,
                        audioRecorderEncodingType = params.audioOutputEncodingType,
                        audioRecorderSampleRateType = params.audioOutputSampleRateType,
                    )
            )

            WakeWordOption.Disabled  -> Unit
        }
    }

    private fun onKeywordDetected(hotWord: String) {
        logger.d { "onKeywordDetected $hotWord" }
        serviceMiddleware.action(WakeWordDetected(Source.Local, hotWord))
    }

}