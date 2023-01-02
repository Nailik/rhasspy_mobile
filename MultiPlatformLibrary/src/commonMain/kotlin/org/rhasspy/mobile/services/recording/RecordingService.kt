package org.rhasspy.mobile.services.recording

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.component.inject
import org.rhasspy.mobile.middleware.Action
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.middleware.Source
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.settings.AppSetting
import kotlin.time.Duration.Companion.milliseconds

//TODO logging
/**
 * records audio and sends data to state machine service
 * also records for wake word
 *
 * recording is started and stopped automatically when output is observed
 */
class RecordingService : IService() {
    private val serviceMiddleware by inject<IServiceMiddleware>()
    private val audioRecorder by inject<AudioRecorder>()

    private var scope = CoroutineScope(Dispatchers.Default)
    private var silenceStartTime: Instant? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.readOnly

    private val _output = MutableStateFlow<List<Byte>>(emptyList())
    val output = _output.readOnly

    val recordedData: List<Byte> = listOf()

    init {
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
                silenceDetection(it)
            }
        }
    }

    override fun onClose() {
        audioRecorder.stopRecording()
        scope.cancel()
    }

    private fun silenceDetection(volume: Short) {
        if (AppSetting.isAutomaticSilenceDetectionEnabled.value) {
            if (volume < AppSetting.automaticSilenceDetectionAudioLevel.value) {
                //no data was above threshold, there is silence
                silenceStartTime?.also {
                    //  logger.d { "silenceDetected" }
                    //check if silence was detected for x milliseconds
                    if (it.minus(Clock.System.now()) < -AppSetting.automaticSilenceDetectionTime.value.milliseconds) {
                        serviceMiddleware.action(Action.DialogAction.SilenceDetected(Source.Local))
                    }
                } ?: run {
                    //  logger.v { "start silence detected" }
                    //first time silence was detected
                    silenceStartTime = Clock.System.now()
                }
            }
        }
    }

    private fun startRecording() {
        audioRecorder.startRecording()
    }

    private fun stopRecording() {
        audioRecorder.stopRecording()
    }

}