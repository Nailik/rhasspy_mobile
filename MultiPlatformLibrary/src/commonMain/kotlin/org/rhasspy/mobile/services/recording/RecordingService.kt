package org.rhasspy.mobile.services.recording

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.component.inject
import org.rhasspy.mobile.isNotAboveThreshold
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.ServiceResponse
import org.rhasspy.mobile.services.statemachine.StateMachineService
import org.rhasspy.mobile.settings.AppSettings
import kotlin.time.Duration.Companion.milliseconds

/**
 * records audio and sends data to state machine service
 * also records for wake word
 * knows if it is recording for wake word, will send wake word data to stateMachineService
 */
class RecordingService : IService() {

    private val logger = Logger.withTag("RecordingService")

    private val stateMachineService by inject<StateMachineService>()

    private val scope = CoroutineScope(Dispatchers.Default)
    private var silenceStartTime: Instant? = null

    private var isRecordingWakeWord = false
    private var isRecordingNormal = false

    init {
        scope.launch {
            //collect from audio recorder
            AudioRecorder.output.collect { value ->
                val byteData = value.toList()
                if (isRecordingNormal) {
                    stateMachineService.audioFrame(byteData)
                    silenceDetection(byteData)
                }
                if (isRecordingWakeWord) {
                    stateMachineService.audioFrameWakeWord(byteData)
                }
            }
        }
    }

    private fun silenceDetection(byteData: List<Byte>) {
        if (AppSettings.isAutomaticSilenceDetectionEnabled.value) {
            //TODO fix not correctly checking volume
            if (byteData.isNotAboveThreshold(AppSettings.automaticSilenceDetectionAudioLevel.value)) {
                //no data was above threshold, there is silence
                silenceStartTime?.also {
                    logger.d { "silenceDetected" }
                    //check if silence was detected for x milliseconds
                    if (it.minus(Clock.System.now()) < -AppSettings.automaticSilenceDetectionTime.value.milliseconds) {
                        stateMachineService.silenceDetected()
                    }
                } ?: run {
                    logger.v { "start silence detected" }
                    //first time silence was detected
                    silenceStartTime = Clock.System.now()
                }
            }
        }
    }

    fun startRecording(): ServiceResponse<*> {
        isRecordingNormal = true
        return startRecorder()
    }

    fun stopRecording() {
        isRecordingNormal = false
        startRecorder()
    }

    fun startRecordingWakeWord(): ServiceResponse<*> {
        isRecordingWakeWord = true
        return startRecorder()
    }

    fun stopRecordingWakeWord() {
        isRecordingWakeWord = false
        stopRecorder()
    }


    private fun startRecorder(): ServiceResponse<*> {
        return if (!AudioRecorder.isRecording.value) {
            AudioRecorder.startRecording()
        } else ServiceResponse.Nothing()
    }

    private fun stopRecorder() {
        if (!isRecordingNormal && !isRecordingWakeWord) {
            AudioRecorder.stopRecording()
        }
    }

    override fun onClose() {
        AudioRecorder.stopRecording()
        scope.cancel()
    }
}