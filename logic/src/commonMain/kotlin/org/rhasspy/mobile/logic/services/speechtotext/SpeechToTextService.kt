package org.rhasspy.mobile.logic.services.speechtotext

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import okio.FileHandle
import okio.Path
import org.koin.core.component.inject
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Record
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.Disabled
import org.rhasspy.mobile.data.service.ServiceState.Success
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrTextCaptured
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.audiofocus.IAudioFocusService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientResult
import org.rhasspy.mobile.logic.services.httpclient.IHttpClientService
import org.rhasspy.mobile.logic.services.localaudio.ILocalAudioService
import org.rhasspy.mobile.logic.services.mqtt.IMqttService
import org.rhasspy.mobile.logic.services.recording.IRecordingService
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils.getWavHeader
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.extensions.commonReadWrite
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting

interface ISpeechToTextService : IService {

    override val serviceState: StateFlow<ServiceState>

    val speechToTextAudioFile: Path
    val isActive: Boolean

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
) : ISpeechToTextService {

    override val logger = LogType.SpeechToTextService.logger()

    private val audioFocusService by inject<IAudioFocusService>()
    private val httpClientService by inject<IHttpClientService>()
    private val mqttClientService by inject<IMqttService>()
    private val recordingService by inject<IRecordingService>()
    private val nativeApplication by inject<NativeApplication>()
    private val serviceMiddleware by inject<IServiceMiddleware>()
    private val localAudioService by inject<ILocalAudioService>()

    private val paramsFlow: StateFlow<SpeechToTextServiceParams> = paramsCreator()
    private val params: SpeechToTextServiceParams get() = paramsFlow.value

    private val _serviceState = MutableStateFlow<ServiceState>(Success)
    override val serviceState = _serviceState.readOnly

    override val speechToTextAudioFile: Path = Path.commonInternalPath(nativeApplication, "SpeechToTextAudio.wav")
    override var isActive: Boolean = false
    private var fileHandle: FileHandle? = null

    private val scope = CoroutineScope(Dispatchers.IO)
    private var collector: Job? = null

    init {
        scope.launch {
            paramsFlow.collect {
                collector?.cancel()
                collector = null

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

        //stop collection
        collector?.cancel()
        collector = null

        audioFocusService.abandon(Record)

        //add wav header to file
        val header = getWavHeader(
            AppSetting.audioRecorderChannel.value,
            AppSetting.audioRecorderSampleRate.value,
            AppSetting.audioRecorderEncoding.value,
            fileHandle?.size() ?: 0
        )

        fileHandle?.write(0, header, 0, header.size)
        fileHandle?.flush()
        fileHandle?.close()
        fileHandle = null

        recordingService.toggleSilenceDetectionEnabled(false)

        //evaluate result
        when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> {
                httpClientService.speechToText(speechToTextAudioFile) { result ->
                    _serviceState.value = result.toServiceState()
                    val action = when (result) {
                        is HttpClientResult.Error   -> AsrError(Source.Local)
                        is HttpClientResult.Success -> AsrTextCaptured(Source.Local, result.data)
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

        //clear data and start recording
        collector?.cancel()
        collector = null

        fileHandle?.flush()
        fileHandle?.close()
        speechToTextAudioFile.commonDelete()
        fileHandle = speechToTextAudioFile.commonReadWrite()

        //start collection
        collector = scope.launch {
            //await until audio playing is finished
            localAudioService.isPlayingState.first { !it }

            if (params.speechToTextOption != SpeechToTextOption.Disabled) {
                recordingService.toggleSilenceDetectionEnabled(true)
            }
            audioFocusService.request(Record)

            recordingService.output.collect {
                if (!localAudioService.isPlayingState.value) {
                    if (it.isNotEmpty()) {
                        audioFrame(sessionId, it)
                    }
                }
            }
        }

        when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> _serviceState.value = Success
            SpeechToTextOption.RemoteMQTT -> if (!fromMqtt) mqttClientService.startListening(sessionId) { _serviceState.value = it }
            SpeechToTextOption.Disabled   -> _serviceState.value = Disabled
        }
    }

    private fun audioFrame(sessionId: String, data: ByteArray) {
        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "audioFrame dataSize: ${data.size}" }
        }

        when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> _serviceState.value = Success
            SpeechToTextOption.RemoteMQTT -> {
                when (params.dialogManagementOption) {
                    DialogManagementOption.Disabled,
                    DialogManagementOption.Local -> mqttClientService.asrAudioSessionFrame(sessionId, data) { _serviceState.value = it }

                    DialogManagementOption.RemoteMQTT -> mqttClientService.asrAudioSessionFrame(sessionId, data) { _serviceState.value = it }
                }

            }

            SpeechToTextOption.Disabled   -> _serviceState.value = Disabled
        }

        scope.launch {
            //write async after data was send
            fileHandle?.write(
                fileOffset = fileHandle?.size() ?: 0,
                array = data,
                arrayOffset = 0,
                byteCount = data.size
            )
        }
    }

}