package org.rhasspy.mobile.logic.domains.wakeword

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import org.koin.core.component.inject
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.WakeWordDetected
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils.appendWavHeader
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.porcupine.PorcupineWakeWordClient
import org.rhasspy.mobile.platformspecific.readOnly
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
    private val audioRecorder: IAudioRecorder,
) : IWakeWordService {

    private val logger = LogType.WakeWordService.logger()

    private val serviceMiddleware by inject<IServiceMiddleware>()
    private val scope = CoroutineScope(Dispatchers.IO)

    //if service is working correctly
    private val _serviceState = MutableStateFlow<ServiceState>(Pending)
    override val serviceState = _serviceState.readOnly

    //parameters
    private val paramsFlow: StateFlow<WakeWordServiceParams> = paramsCreator()
    private val params: WakeWordServiceParams get() = paramsFlow.value

    //initial parameters
    private var initializedParams: WakeWordServiceParams? = null

    //used apis
    private var udpConnection: UdpConnection? = null
    private var porcupineWakeWordClient: PorcupineWakeWordClient? = null

    //if wake word detection is currently active
    private var isDetectionRunning = false

    //if it is actually recording
    override val isRecording = audioRecorder.isRecording

    //recording job
    private var recording: Job? = null

    /**
     * starts the service
     */
    init {
        scope.launch {
            paramsFlow.collect {
                updateState()

                //resume detection if necessary
                resumeDetection()

                if (!it.isEnabled && isRecording.value) {
                    stopRecording()
                }
            }
        }
    }

    private fun updateState() {
        _serviceState.value = when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {
                if (!params.isMicrophonePermissionEnabled) {
                    //post error when microphone permission is missing
                    Error(MR.strings.microphonePermissionDenied.stable)
                } else Pending
            }

            WakeWordOption.MQTT      -> Success
            WakeWordOption.Udp       -> {
                if (!params.isMicrophonePermissionEnabled) {
                    //post error when microphone permission is missing
                    Error(MR.strings.microphonePermissionDenied.stable)
                } else Pending
            }

            WakeWordOption.Disabled  -> Disabled
        }
    }

    /**
     * when detection should run by dialog manager
     */
    override fun startDetection() {
        logger.d { "startDetection $isDetectionRunning" }
        isDetectionRunning = true

        //do nothing it's not enabled or microphone permission is missing
        if (!params.isEnabled || !params.isMicrophonePermissionEnabled) return

        //check that everything is initialized
        initialize()

        //actually start recording
        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> startPorcupine()
            WakeWordOption.Udp       -> startUdp()
            else                     -> Unit
        }
    }

    /**
     * pause detection by dialog manager
     */
    override fun stopDetection() {
        logger.d { "stopDetection $isDetectionRunning" }
        if (!isDetectionRunning) return
        stopRecording()
        isDetectionRunning = false
    }

    /**
     * resume the detection internally
     */
    private fun resumeDetection() {
        if (isDetectionRunning) {
            startDetection()
        }
    }

    /**
     * stop internally
     */
    private fun stopRecording() {
        logger.d { "stop" }
        recording?.cancel()
        recording = null
        audioRecorder.stopRecording()
    }


    private fun startPorcupine() {
        _serviceState.value = porcupineWakeWordClient?.let { client ->
            val error = client.start()
            error?.let {
                logger.e(it) { "porcupineError" }
                ServiceState.Exception(it)
            } ?: Success
        } ?: Error(MR.strings.notInitialized.stable)
        startRecording()
    }

    private fun startUdp() {
        _serviceState.value = udpConnection?.let { client ->
            val error = client.connect()
            error?.let {
                logger.e(it) { "porcupineError" }
                ServiceState.Exception(it)
            } ?: Success
        } ?: Error(MR.strings.notInitialized.stable)
        startRecording()
    }

    private fun startRecording() {
        if (_serviceState.value == Success) {
            audioRecorder.startRecording(
                audioRecorderChannelType = params.audioRecorderChannelType,
                audioRecorderEncodingType = params.audioRecorderEncodingType,
                audioRecorderSampleRateType = params.audioRecorderSampleRateType,
                audioRecorderOutputChannelType = params.audioOutputChannelType,
                audioRecorderOutputEncodingType = params.audioOutputEncodingType,
                audioRecorderOutputSampleRateType = params.audioOutputSampleRateType,
                isAutoPauseOnMediaPlayback = params.isAutoPauseOnMediaPlayback
            )

            recording?.cancel()
            recording = scope.launch {
                audioRecorder.output.collectLatest(::hotWordAudioFrame)
            }
        }
    }

    /**
     * disposes old if necessary and reinitialize wake word
     */
    private fun initialize() {
        logger.d { "check initialize" }
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

        _serviceState.value = when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {
                _serviceState.value = Loading
                //when porcupine is used for hotWord then start local service
                porcupineWakeWordClient = PorcupineWakeWordClient(
                    params.wakeWordPorcupineAccessToken,
                    params.wakeWordPorcupineKeywordDefaultOptions,
                    params.wakeWordPorcupineKeywordCustomOptions,
                    params.wakeWordPorcupineLanguage,
                    ::onKeywordDetected,
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

            WakeWordOption.Disabled  -> Disabled
        }
    }

    private fun disposeOld() {
        logger.d { "disposeOld" }
        recording?.cancel()
        recording = null
        try {
            porcupineWakeWordClient?.close()
        } catch (e: Exception) {
            logger.e(e) { "porcupineWakeWordClient disposeOld" }
        }
        udpConnection?.close()
        udpConnection = null
        porcupineWakeWordClient?.close()
        porcupineWakeWordClient = null
    }

    private suspend fun hotWordAudioFrame(data: ByteArray) {
        if (!isDetectionRunning) return

        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "hotWordAudioFrame dataSize: ${data.size}" }
        }

        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> porcupineWakeWordClient?.audioFrame(data)
            WakeWordOption.MQTT      -> Unit //nothing will wait for mqtt message
            WakeWordOption.Udp       -> udpConnection?.streamAudio(
                data = data.appendWavHeader(
                    audioRecorderChannelType = params.audioOutputChannelType,
                    audioRecorderEncodingType = params.audioOutputEncodingType,
                    audioRecorderSampleRateType = params.audioOutputSampleRateType,
                )
            )

            WakeWordOption.Disabled  -> Unit
        }
    }

    /**
     * local wake word was detected
     */
    private fun onKeywordDetected(hotWord: String) {
        logger.d { "onKeywordDetected $hotWord" }
        serviceMiddleware.action(WakeWordDetected(Source.Local, hotWord))
    }


}