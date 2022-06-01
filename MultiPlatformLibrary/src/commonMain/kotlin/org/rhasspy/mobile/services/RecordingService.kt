package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.isNotAboveThreshold
import org.rhasspy.mobile.logic.State
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.nativeutils.AudioPlayer
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings
import kotlin.native.concurrent.ThreadLocal
import kotlin.time.Duration.Companion.milliseconds

/**
 * records wakeword or intent
 */
@ThreadLocal
object RecordingService {

    private val logger = Logger.withTag("RecordingService")
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    //state if recording
    private var isRecording: Boolean = false

    //job for async recording
    private var recordingJob: Job? = null

    //time when the silence was detected
    private var silenceStartTime: Instant? = null

    //data storage to reuse list
    private var data = mutableListOf<Byte>()

    private var isRecordingForWakeWord = false

    init {
        StateMachine.currentState.observe {
            when (it) {
                //start recording intent
                State.RecordingIntent -> startRecording()
                //stop recording intent
                State.RecordingStopped,
                State.PlayingAudio,
                State.PlayingRecording -> stopRecording()
                else -> {}
            }
        }

        //audio indication sounds should be ignored
        AudioPlayer.isPlayingState.observe {
            when (it) {
                //stop recording while sounds are playing
                true -> stopRecording()
                //resume recording if necessary
                false -> if (isRecordingForWakeWord || StateMachine.currentState.value == State.RecordingIntent) {
                    //resume recording
                    startRecording()
                }
            }
        }
    }


    /**
     * should be called when wake word is detected or user wants to speak
     * by clicking ui
     */
    private fun startRecording() {
        if (isRecording) {
            logger.d { "alreadyRecording" }
            return
        }

        logger.d { "startRecording" }
        isRecording = true
        silenceStartTime = null

        data.clear()

        recordingJob = coroutineScope.launch {
            AudioRecorder.output.collectIndexed { _, value ->

                val byteData = value.toList()
                data.addAll(byteData)
                StateMachine.audioFrame(byteData)

                if (AppSettings.isAutomaticSilenceDetection.value && ConfigurationSettings.wakeWordOption.value != WakeWordOption.MQTT) {
                    if (byteData.isNotAboveThreshold(AppSettings.automaticSilenceDetectionAudioLevel.value)) {
                        //no data was above threshold, there is silence
                        silenceStartTime?.also {
                            logger.d { "silenceDetected" }
                            //check if silence was detected for x milliseconds
                            if (it.minus(Clock.System.now()) < -AppSettings.automaticSilenceDetectionTime.value.milliseconds) {
                                StateMachine.stopListening()
                            }
                        } ?: run {
                            logger.v { "start silence detected" }
                            //first time silence was detected
                            silenceStartTime = Clock.System.now()
                        }
                    }
                }
            }
        }

        AudioRecorder.startRecording()
    }

    /**
     * called when service should stop listening
     */
    private fun stopRecording() {
        logger.d { "stopRecording" }

        if (isRecording) {
            AudioRecorder.stopRecording()
            isRecording = false
            recordingJob?.cancel()
        }
    }

    /**
     * starts recording (if not already recording)
     * and saves recording for wakeWord, so recording will resume after indication sound
     */
    fun startRecordingWakeWord() {
        isRecordingForWakeWord = true
        startRecording()
    }

    fun stopRecordingWakeWord() {
        isRecordingForWakeWord = false
        stopRecording()
    }

}