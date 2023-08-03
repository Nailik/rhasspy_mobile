package org.rhasspy.mobile.logic.services.speechtotext

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import okio.FileHandle
import okio.Path
import org.koin.core.component.inject
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Record
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.Disabled
import org.rhasspy.mobile.data.service.ServiceState.Success
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source.Local
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.audiofocus.IAudioFocusService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientResult
import org.rhasspy.mobile.logic.services.httpclient.IHttpClientService
import org.rhasspy.mobile.logic.services.localaudio.ILocalAudioService
import org.rhasspy.mobile.logic.services.mqtt.IMqttService
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils.appendWavHeader
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils.getWavHeader
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.extensions.commonReadWrite
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.resampler.Resampler
import org.rhasspy.mobile.settings.AppSetting

interface ISpeechToTextService : IService {

    override val serviceState: StateFlow<ServiceState>

    val speechToTextAudioFile: Path
    val isActive: Boolean
    val isRecording: StateFlow<Boolean>

    fun endSpeechToText(sessionId: String, fromMqtt: Boolean)
    fun startSpeechToText(sessionId: String, fromMqtt: Boolean)

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class SpeechToTextService(
    paramsCreator: SpeechToTextServiceParamsCreator,
    private val audioRecorder: IAudioRecorder
) : ISpeechToTextService {

    override val logger = LogType.SpeechToTextService.logger()

    private val audioFocusService by inject<IAudioFocusService>()
    private val httpClientService by inject<IHttpClientService>()
    private val mqttClientService by inject<IMqttService>()
    private val nativeApplication by inject<NativeApplication>()
    private val serviceMiddleware by inject<IServiceMiddleware>()
    private val localAudioService by inject<ILocalAudioService>()

    private val paramsFlow: StateFlow<SpeechToTextServiceParams> = paramsCreator()
    private val params: SpeechToTextServiceParams get() = paramsFlow.value

    private val _serviceState = MutableStateFlow<ServiceState>(Success)
    override val serviceState = _serviceState.readOnly

    override val speechToTextAudioFile: Path = Path.commonInternalPath(nativeApplication, "SpeechToTextAudio.wav")
    override var isActive: Boolean = false
    override val isRecording: StateFlow<Boolean> = audioRecorder.isRecording
    private var fileHandle: FileHandle? = null

    private val scope = CoroutineScope(Dispatchers.IO)
    private var recorder: Job? = null

    private val silenceDetection = SilenceDetection {
        serviceMiddleware.action(SilenceDetected(Local))
    }

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

    init {
        scope.launch {
            paramsFlow.collect {
                recorder?.cancel()
                recorder = null

                resampler?.dispose()
                resampler = null

                _serviceState.value =
                    when (it.speechToTextOption) {
                        SpeechToTextOption.RemoteHTTP -> Success
                        SpeechToTextOption.RemoteMQTT -> Success
                        SpeechToTextOption.Disabled   -> Disabled
                    }
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
    override fun endSpeechToText(sessionId: String, fromMqtt: Boolean) {
        if (!isActive) return
        isActive = false
        logger.d { "endSpeechToText sessionId: $sessionId fromMqtt $fromMqtt" }

        audioRecorder.stopRecording()

        resampler?.dispose()
        resampler = null

        //stop recorder
        recorder?.cancel()
        recorder = null

        audioFocusService.abandon(Record)

        //add wav header to file
        val header = getWavHeader(
            audioRecorderChannelType = params.audioRecorderChannelType,
            audioRecorderEncodingType = params.audioRecorderEncodingType,
            audioRecorderSampleRateType = params.audioRecorderSampleRateType,
            audioSize = fileHandle?.size() ?: 0
        )

        fileHandle?.write(0, header, 0, header.size)
        fileHandle?.flush()
        fileHandle?.close()
        fileHandle = null

        //evaluate result
        when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> {
                httpClientService.speechToText(speechToTextAudioFile) { result ->
                    _serviceState.value = result.toServiceState()
                    val action = when (result) {
                        is HttpClientResult.Error   -> AsrError(Local)
                        is HttpClientResult.Success -> AsrTextCaptured(Local, result.data)
                    }
                    serviceMiddleware.action(action)
                }

            }

            SpeechToTextOption.RemoteMQTT -> if (!fromMqtt) {
                mqttClientService.stopListening(sessionId) {
                    _serviceState.value = it
                }
            }

            SpeechToTextOption.Disabled   -> Unit
        }
    }

    override fun startSpeechToText(sessionId: String, fromMqtt: Boolean) {
        if (isActive) return
        isActive = true
        logger.d { "startSpeechToText sessionId: $sessionId fromMqtt $fromMqtt" }

        //clear data
        recorder?.cancel()
        recorder = null

        fileHandle?.flush()
        fileHandle?.close()
        speechToTextAudioFile.commonDelete()
        fileHandle = speechToTextAudioFile.commonReadWrite()


        when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> _serviceState.value = Success
            SpeechToTextOption.RemoteMQTT -> if (!fromMqtt) mqttClientService.startListening(sessionId) { _serviceState.value = it }
            SpeechToTextOption.Disabled   -> {
                _serviceState.value = Disabled
                return //recorder doesn't need to be started
            }
        }

        //init resampler
        getResampler()

        //start recorder
        recorder = scope.launch {
            record(sessionId, this)
        }
    }

    private fun audioFrame(sessionId: String, data: ByteArray) {
        if (!isActive) return

        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "audioFrame dataSize: ${data.size}" }
        }

        val resampled = getResampler().resample(data)

        when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> _serviceState.value = Success
            SpeechToTextOption.RemoteMQTT -> {
                mqttClientService.asrAudioSessionFrame(
                    sessionId = sessionId,
                    resampled.appendWavHeader(
                        audioRecorderChannelType = params.audioOutputChannelType,
                        audioRecorderEncodingType = params.audioOutputEncodingType,
                        audioRecorderSampleRateType = params.audioOutputSampleRateType
                    )
                ) { _serviceState.value = it }
            }

            SpeechToTextOption.Disabled   -> _serviceState.value = Disabled
        }

        //write async after data was send
        fileHandle?.write(
            fileOffset = fileHandle?.size() ?: 0,
            array = resampled,
            arrayOffset = 0,
            byteCount = resampled.size
        )
    }

    private suspend fun record(sessionId: String, coroutineScope: CoroutineScope) {
        silenceDetection.reset()
        localAudioService.isPlayingState.first { !it }

        audioFocusService.request(Record)

        coroutineScope.launch {
            //collect from audio recorder
            audioRecorder.output.collectLatest { data ->
                if (!localAudioService.isPlayingState.value) {
                    if (data.isNotEmpty()) {
                        audioFrame(sessionId, data)
                    }
                }
            }
        }

        if (AppSetting.isAutomaticSilenceDetectionEnabled.value) {
            coroutineScope.launch {
                audioRecorder.maxVolume.collect {
                    silenceDetection.audioFrameVolume(it)
                }
            }
        }

        audioRecorder.startRecording(
            audioRecorderChannelType = params.audioRecorderChannelType,
            audioRecorderEncodingType = params.audioRecorderEncodingType,
            audioRecorderSampleRateType = params.audioRecorderSampleRateType,
        )
    }

}