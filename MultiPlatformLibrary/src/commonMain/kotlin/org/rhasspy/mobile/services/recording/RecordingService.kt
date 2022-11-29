package org.rhasspy.mobile.services.recording

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.component.inject
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.settings.AppSettings
import kotlin.time.Duration.Companion.milliseconds

/**
 * records audio and sends data to state machine service
 * also records for wake word
 * knows if it is recording for wake word, will send wake word data to stateMachineService
 */
class RecordingService : IService() {

    /**
     * recrding is started and stopped automatically when output is observed
     */
    private val logger = Logger.withTag("RecordingService")

    private val serviceMiddleware by inject<IServiceMiddleware>()

    private var scope = CoroutineScope(Dispatchers.Default)
    private var silenceStartTime: Instant? = null

    private var isRecordingWakeWord = false
    private var isRecordingNormal = false

    private val _output = MutableStateFlow<List<Byte>>(emptyList())
    val output: StateFlow<List<Byte>> = AudioRecorder.output

    // when no one observes output then stop recording?

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
            AudioRecorder.output.collect { value ->
                _output.value = value
            }
        }

        scope.launch {
            AudioRecorder.maxVolume.collect {
                silenceDetection(it)
            }
        }
    }

    override fun onClose() {
        AudioRecorder.stopRecording()
        scope.cancel()
    }

    private fun silenceDetection(volume: Short) {
        if (AppSettings.isAutomaticSilenceDetectionEnabled.value) {
            if (volume < AppSettings.automaticSilenceDetectionAudioLevel.value) {
                //no data was above threshold, there is silence
                silenceStartTime?.also {
                  //  logger.d { "silenceDetected" }
                    //check if silence was detected for x milliseconds
                    if (it.minus(Clock.System.now()) < -AppSettings.automaticSilenceDetectionTime.value.milliseconds) {
                        serviceMiddleware.silenceDetected()
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
        if (!AudioRecorder.isRecording.value) {
            AudioRecorder.startRecording()
        }
    }

    private fun stopRecording() {
        if (!isRecordingNormal && !isRecordingWakeWord) {
            AudioRecorder.stopRecording()
        }
    }

}