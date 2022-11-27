package org.rhasspy.mobile.services.recording

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.component.inject
import org.rhasspy.mobile.addWavHeader
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.ServiceResponse
import org.rhasspy.mobile.services.dialogManager.IDialogManagerService
import org.rhasspy.mobile.settings.AppSettings
import kotlin.time.Duration.Companion.milliseconds

/**
 * records audio and sends data to state machine service
 * also records for wake word
 * knows if it is recording for wake word, will send wake word data to stateMachineService
 */
class RecordingService : IService() {

    private val logger = Logger.withTag("RecordingService")

    private val dialogManagerService by inject<IDialogManagerService>()

    private var scope = CoroutineScope(Dispatchers.Default)
    private var silenceStartTime: Instant? = null

    private var isRecordingWakeWord = false
    private var isRecordingNormal = false

    init {
        scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            //collect from audio recorder
            AudioRecorder.output.collect { value ->
                val byteData = value.toMutableList().apply {
                    addWavHeader()
                }
                if (isRecordingNormal) {
                    dialogManagerService.audioFrameLocal(byteData)
                }
                if (isRecordingWakeWord) {
                    dialogManagerService.audioFrameWakeWordLocal(byteData)
                }
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
                    logger.d { "silenceDetected" }
                    //check if silence was detected for x milliseconds
                    if (it.minus(Clock.System.now()) < -AppSettings.automaticSilenceDetectionTime.value.milliseconds) {
                        dialogManagerService.silenceDetectedLocal()
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
        stopRecorder()
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
        } else ServiceResponse.Nothing
    }

    private fun stopRecorder() {
        if (!isRecordingNormal && !isRecordingWakeWord) {
            AudioRecorder.stopRecording()
        }
    }
}