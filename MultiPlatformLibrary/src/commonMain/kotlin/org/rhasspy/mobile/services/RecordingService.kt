package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.rhasspy.mobile.services.dialogue.ServiceInterface
import org.rhasspy.mobile.services.native.AudioRecorder
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.toByteArray
import kotlin.native.concurrent.ThreadLocal
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

/**
 * Handles listening to speech
 */
@ThreadLocal
object RecordingService {
    private val logger = Logger.withTag(this::class.simpleName!!)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val listening = MutableLiveData(false)

    //represents listening Status for ui
    val status: LiveData<Boolean> = listening.readOnly()

    private val flow = MutableSharedFlow<ByteArray>()
    val sharedFlow: Flow<ByteArray> get() = flow.takeWhile { listening.value }

    private var data = mutableListOf<Byte>()

    private var firstSilenceDetected: Instant? = null

    private var job: Job? = null

    //https://stackoverflow.com/questions/19145213/android-audio-capture-silence-detection
    private fun searchThreshold(arr: ByteArray, thr: Int): Boolean {
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

        job = coroutineScope.launch {
            AudioRecorder.output.collectIndexed { _, value ->
                data.addAll(value.toList())
                flow.emit(value)

                if (AppSettings.isAutomaticSilenceDetection.data) {
                    if (!searchThreshold(value, AppSettings.automaticSilenceDetectionAudioLevel.data)) {
                        if (firstSilenceDetected == null) {
                            firstSilenceDetected = Clock.System.now()

                        } else if (firstSilenceDetected?.minus(Clock.System.now()) ?: ZERO <
                            (-AppSettings.automaticSilenceDetectionTime.data).milliseconds
                        ) {
                            logger.i { "diff ${firstSilenceDetected?.minus(Clock.System.now())}" }

                            CoroutineScope(Dispatchers.Main).launch {
                                //stop instantly
                                listening.value = false
                                ServiceInterface.stopRecording()
                            }
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
    fun stopRecording() {
        logger.d { "stopRecording" }

        listening.value = false
        AudioRecorder.stopRecording()
        job?.cancel()
    }

    fun getLatestRecording(): ByteArray {
        return data.toByteArray().addWavHeader()
    }

    fun ByteArray.addWavHeader(): ByteArray {
        //https://stackoverflow.com/questions/13039846/what-do-the-bytes-in-a-wav-file-represent
        val dataSize = (this.size + 44 - 8).toByteArray()
        val audioDataSize = this.size.toByteArray()

        val header = byteArrayOf(
            82, 73, 70, 70,
            dataSize[0], dataSize[1], dataSize[2], dataSize[3], //4-7 overall size
            87, 65, 86, 69, 102, 109, 116, 32, 16, 0, 0, 0, 1, 0, 1, 0, -128, 62, 0, 0, 0, 125, 0, 0, 2, 0, 16, 0, 100, 97, 116, 97,
            audioDataSize[0], audioDataSize[1], audioDataSize[2], audioDataSize[3] //40-43 data size of rest
        )

        return mutableListOf<Byte>().apply {
            addAll(header.toList())
            addAll(this.toList())
        }.toByteArray()
    }

}