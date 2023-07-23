package org.rhasspy.mobile.logic.services.recording

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.component.inject
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting
import kotlin.time.Duration.Companion.milliseconds

interface IRecordingService : IService {

    override val serviceState: StateFlow<ServiceState>

    val isRecording: StateFlow<Boolean>
    val output: StateFlow<ByteArray>

    fun toggleSilenceDetectionEnabled(enabled: Boolean)

}


/**
 * records audio and sends data to state machine service
 * also records for wake word
 *
 * recording is started and stopped automatically when output is observed
 */
internal class RecordingService(
    private val audioRecorder: IAudioRecorder
) : IRecordingService {

    override val logger = LogType.RecordingService.logger()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    override val serviceState = _serviceState.readOnly

    private val serviceMiddleware by inject<IServiceMiddleware>()

    private val scope = CoroutineScope(Dispatchers.IO)
    private var silenceStartTime: Instant? = null
    private var recordingStartTime: Instant? = null

    private val _isRecording = MutableStateFlow(false)
    override val isRecording = _isRecording.readOnly

    private val _output = MutableStateFlow(ByteArray(0))
    override val output = _output.readOnly

    private var isSilenceDetectionEnabled = false

    init {
        logger.d { "initialize" }

        _output.subscriptionCount
            .map { count -> count > 0 } // map count into active/inactive flag
            .distinctUntilChanged() // only react to true<->false changes
            .onEach { isActive -> // configure an action
                if (isActive) startRecording() else stopRecording()
            }
            .launchIn(scope) // launch it

        scope.launch {
            //collect from audio recorder
            audioRecorder.output.collect { value ->
                _output.value = value
            }
        }

        scope.launch {
            audioRecorder.maxVolume.collect {
                if (isSilenceDetectionEnabled) {
                    silenceDetection(it)
                }
            }
        }
    }

    private fun silenceDetection(volume: Float) {
        val automaticSilenceDetectionTime = AppSetting.automaticSilenceDetectionTime.value ?: 0
        //check enabled
        if (AppSetting.isAutomaticSilenceDetectionEnabled.value) {

            //check minimum recording time
            val currentTime = Clock.System.now()
            val timeSinceStart = recordingStartTime?.let { currentTime.minus(it) } ?: 0.milliseconds
            if (timeSinceStart > automaticSilenceDetectionTime.milliseconds) {

                //volume below threshold
                if (volume < AppSetting.automaticSilenceDetectionAudioLevel.value) {

                    //not initial silence
                    if (silenceStartTime != null) {

                        //silence duration
                        val timeSinceSilenceDetected =
                            silenceStartTime?.let { currentTime.minus(it) } ?: 0.milliseconds
                        //check if silence was detected for x milliseconds
                        if (timeSinceSilenceDetected > automaticSilenceDetectionTime.milliseconds) {
                            serviceMiddleware.action(
                                ServiceMiddlewareAction.DialogServiceMiddlewareAction.SilenceDetected(
                                    Source.Local
                                )
                            )
                        }

                    } else {
                        //first time silence was detected
                        silenceStartTime = Clock.System.now()
                    }

                } else {
                    //reset silence time (was above threshold)
                    silenceStartTime = null
                }
            }
        }
    }

    override fun toggleSilenceDetectionEnabled(enabled: Boolean) {
        isSilenceDetectionEnabled = enabled
    }

    private fun startRecording() {
        silenceStartTime = null
        logger.d { "startRecording" }
        _isRecording.value = true
        audioRecorder.startRecording(
            audioRecorderChannelType = AppSetting.audioRecorderChannel.value,
            audioRecorderEncodingType = AppSetting.audioRecorderEncoding.value,
            audioRecorderSampleRateType = AppSetting.audioRecorderSampleRate.value
        )
        recordingStartTime = Clock.System.now()
    }

    private fun stopRecording() {
        silenceStartTime = null
        isSilenceDetectionEnabled = false
        logger.d { "stopRecording" }
        _isRecording.value = false
        recordingStartTime = null
        audioRecorder.stopRecording()
    }

}