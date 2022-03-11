package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.services.native.AudioRecorder
import org.rhasspy.mobile.services.native.NativeIndication
import org.rhasspy.mobile.settings.AppSettings
import kotlin.native.concurrent.ThreadLocal
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Handles listening to speech
 */
@ThreadLocal
object RecordingService {
    private val logger = Logger.withTag(this::class.simpleName!!)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val listening = MutableLiveData(false)

    //represents listening Status for ui
    val status: LiveData<Boolean> = listening.map { it }

    private var data = mutableListOf<Byte>()

    private var firstSilenceDetected: Instant? = null

    init {
        coroutineScope.launch {
            AudioRecorder.output.collectIndexed { _, value ->
                if (listening.value) {
                    data.addAll(value.toList())

                    if (AppSettings.automaticSilenceDetection.data) {
                        if (!searchThreshold(value, 40)) {
                            if (firstSilenceDetected == null) {
                                firstSilenceDetected = Clock.System.now()
                            } else if (firstSilenceDetected?.minus(Clock.System.now()) ?: ZERO < (-2).seconds) {
                                logger.i { "diff ${firstSilenceDetected?.minus(Clock.System.now())}" }

                                CoroutineScope(Dispatchers.Main).launch {
                                    //stop instantly
                                    listening.value = false
                                    ServiceInterface.registeredSilence()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //https://stackoverflow.com/questions/19145213/android-audio-capture-silence-detection
    private fun searchThreshold(arr: ByteArray, thr: Short): Boolean {
        arr.forEach {
            if (it >= thr || it <= -thr) {
                return true
            }
        }
        return false
    }

    /**
     * should be called when wake word is detected or user wants to speak
     * by clicking ui
     */
    fun startRecording() {
        logger.d { "startRecording" }
        firstSilenceDetected = null
        listening.value = true
        data.clear()
        indication()
        AudioRecorder.startRecording()
    }

    /**
     * called when service should stop listening
     */
    fun stopRecording() {
        logger.d { "stopRecording" }

        listening.value = false
        stopIndication()
        AudioRecorder.stopRecording()
    }

    /**
     * starts wake word indication according to settings
     */
    private fun indication() {
        logger.d { "indication" }

        if (AppSettings.isWakeWordSoundIndication.data) {
            NativeIndication.playAudio(MR.files.etc_wav_beep_hi)
        }

        if (AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.data) {
            NativeIndication.wakeUpScreen()
        }

        if (AppSettings.isWakeWordLightIndication.data) {
            NativeIndication.showIndication()
        }
    }

    /**
     * stops all indications
     */
    private fun stopIndication() {
        logger.d { "stopIndication" }

        NativeIndication.closeIndicationOverOtherApps()
        NativeIndication.releaseWakeUp()
    }

    fun getLatestRecording() = data.toByteArray()

}