package org.rhasspy.mobile.services.recording

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.component.inject
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.middleware.Action
import org.rhasspy.mobile.middleware.ServiceMiddleware
import org.rhasspy.mobile.middleware.Source
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.settings.AppSetting
import kotlin.time.Duration.Companion.milliseconds

/**
 * records audio and sends data to state machine service
 * also records for wake word
 *
 * recording is started and stopped automatically when output is observed
 */
class RecordingService : IService() {
    private val logger = LogType.RecordingService.logger()

    private val serviceMiddleware by inject<ServiceMiddleware>()
    private val audioRecorder by inject<AudioRecorder>()

    private var scope = CoroutineScope(Dispatchers.Default)
    private var silenceStartTime: Instant? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.readOnly

    private val _output = MutableStateFlow<ByteArray>(ByteArray(0))
    val output = _output.readOnly

    private var isSilenceDetectionEnabled = false

    init {
        logger.d { "initialize" }
        scope = CoroutineScope(Dispatchers.Default)

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

    override fun onClose() {
        logger.d { "onClose" }
        audioRecorder.stopRecording()
        scope.cancel()
    }

    private fun silenceDetection(volume: Short) {
        if (AppSetting.isAutomaticSilenceDetectionEnabled.value) {
            if (volume < AppSetting.automaticSilenceDetectionAudioLevel.value) {
                //no data was above threshold, there is silence
                silenceStartTime?.also {
                    //check if silence was detected for x milliseconds
                    if (it.minus(Clock.System.now()) < -AppSetting.automaticSilenceDetectionTime.value.milliseconds) {
                        serviceMiddleware.action(Action.DialogAction.SilenceDetected(Source.Local))
                    }
                } ?: run {
                    //first time silence was detected
                    silenceStartTime = Clock.System.now()
                }
            } else {
                //reset silence time
                silenceStartTime = null
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
    }

    private fun stopRecording() {
        silenceStartTime = null
        isSilenceDetectionEnabled = false
        logger.d { "stopRecording" }
        _isRecording.value = false
        audioRecorder.stopRecording()
    }

}