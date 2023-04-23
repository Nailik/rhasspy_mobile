package org.rhasspy.mobile.logic.services.recording

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorder
import org.rhasspy.mobile.platformspecific.readOnly
import kotlin.time.Duration.Companion.milliseconds

/**
 * records audio and sends data to state machine service
 * also records for wake word
 *
 * recording is started and stopped automatically when output is observed
 */
class RecordingService(
    private val audioRecorder: AudioRecorder
) : IService(LogType.RecordingService) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var silenceStartTime: Instant? = null
    private var recordingStartTime: Instant? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.readOnly

    private val _output = MutableStateFlow(ByteArray(0))
    val output = _output.readOnly

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

    private fun silenceDetection(volume: Short) {
        //check enabled
        if (AppSetting.isAutomaticSilenceDetectionEnabled.value) {

            //check minimum recording time
            val currentTime = Clock.System.now()
            val timeSinceStart = recordingStartTime?.let { currentTime.minus(it) } ?: 0.milliseconds
            if (timeSinceStart > AppSetting.automaticSilenceDetectionMinimumTime.value.milliseconds) {

                //volume below threshold
                if (volume < AppSetting.automaticSilenceDetectionAudioLevel.value) {

                    //not initial silence
                    if (silenceStartTime != null) {

                        //silence duration
                        val timeSinceSilenceDetected = silenceStartTime?.let { currentTime.minus(it) } ?: 0.milliseconds
                        //check if silence was detected for x milliseconds
                        if (timeSinceSilenceDetected > AppSetting.automaticSilenceDetectionTime.value.milliseconds) {
                            serviceMiddleware.action(ServiceMiddlewareAction.DialogServiceMiddlewareAction.SilenceDetected(Source.Local))
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

    fun toggleSilenceDetectionEnabled(enabled: Boolean) {
        isSilenceDetectionEnabled = enabled
    }

    private fun startRecording() {
        silenceStartTime = null
        logger.d { "startRecording" }
        _isRecording.value = true
        audioRecorder.startRecording()
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