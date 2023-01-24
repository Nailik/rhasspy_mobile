package org.rhasspy.mobile.logic.nativeutils

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioRecord.RECORDSTATE_RECORDING
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import co.touchlab.kermit.Logger
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.logic.readOnly
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual class AudioRecorder : KoinComponent, Closeable {
    private val logger = Logger.withTag("AudioRecorder")

    /**
     * output data as flow
     */
    private val _output = MutableSharedFlow<ByteArray>()
    actual val output = _output.readOnly

    /**
     * max volume since start recording
     */
    private val _maxVolume = MutableStateFlow<Short>(0)
    actual val maxVolume = _maxVolume.readOnly

    //state if currently recording
    private val _isRecording = MutableStateFlow(false)
    actual val isRecording = _isRecording.readOnly

    //maximum audio level that can happen
    //https://developer.android.com/reference/android/media/AudioFormat#encoding
    actual val absoluteMaxVolume = 32767.0

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var recorder: AudioRecord? = null

    /**
     * start recording
     *
     * creates audio recorder if null
     */
    actual fun startRecording() {
        logger.v { "startRecording" }

        try {
            if (recorder == null) {
                logger.v { "initializing recorder" }

                if (ActivityCompat.checkSelfPermission(
                        get<NativeApplication>(),
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    logger.e { "missing recording permission" }
                }

                recorder = AudioRecord.Builder()
                    .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(SAMPLING_RATE_IN_HZ)
                            .setChannelMask(CHANNEL_CONFIG)
                            .setEncoding(AUDIO_FORMAT)
                            .build()
                    )
                    .setBufferSizeInBytes(BUFFER_SIZE) //8000
                    .build()
            }

            _isRecording.value = true
            recorder?.startRecording()
            read()
        } catch (e: Exception) {
            _isRecording.value = false
            logger.e(e) { "native start recording error" }
        }
    }

    /**
     * stop recording
     */
    actual fun stopRecording() {
        logger.v { "stopRecording" }
        if (_isRecording.value) {
            _isRecording.value = false
            recorder?.stop()
            //without release audio output sometimes doesn't work after calling start
            recorder?.release()
            recorder = null
        }
    }

    /**
     * close audio recorder, releases recorder
     */
    override fun close() {
        stopRecording()
    }

    /**
     * reads from audio and emits buffer onto output
     */
    private fun read() {
        coroutineScope.launch {
            recorder?.also {
                while (it.recordingState == RECORDSTATE_RECORDING) {
                    val byteArray = ByteArray(BUFFER_SIZE)
                    it.read(byteArray, 0, byteArray.size)

                    coroutineScope.launch {
                        var max: Short = 0
                        for (i in 0..byteArray.size step 2) {
                            if (i < byteArray.size) {
                                val bb = ByteBuffer.wrap(byteArray.copyOfRange(i, i + 2))
                                bb.order(ByteOrder.nativeOrder())
                                val short = bb.short

                                if (short > max) {
                                    max = short
                                }
                            }
                        }
                        _maxVolume.value = max
                    }

                    _output.emit(byteArray)
                }
            }
        }
    }

    actual companion object {
        private const val SAMPLING_RATE_IN_HZ = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val BIT_RATE = 16 //according to ENCODING_PCM_16BIT
        private const val BYTE_RATE = (BIT_RATE * SAMPLING_RATE_IN_HZ * CHANNEL_CONFIG) / 8
        private const val BUFFER_SIZE_FACTOR = 2
        private val BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLING_RATE_IN_HZ,
            CHANNEL_CONFIG, AUDIO_FORMAT
        ) * BUFFER_SIZE_FACTOR
        //https://github.com/razzo04/rhasspy-mobile-app/blob/3c59971270eab0278cd5dbf6adac4064b5f14908/android/app/src/main/java/com/example/rhasspy_mobile_app/WakeWordService.java#L151
        /**
         * use the settings of the audio recorder
         * (sampleRate, channels, bitrate) and the audioSize
         * to create wav header
         */
        actual fun ByteArray.appendWavHeader(): ByteArray {
            val audioSize = this.size
            val totalLength = audioSize + 36
            val header = arrayOf(
                'R'.code.toByte(),
                'I'.code.toByte(),
                'F'.code.toByte(),
                'F'.code.toByte(),
                (totalLength and 0xff).toByte(),
                ((totalLength shr 8) and 0xff).toByte(),
                ((totalLength shr 16) and 0xff).toByte(),
                ((totalLength shr 24) and 0xff).toByte(),
                'W'.code.toByte(),
                'A'.code.toByte(),
                'V'.code.toByte(),
                'E'.code.toByte(),
                'f'.code.toByte(), // 'fmt ' chunk
                'm'.code.toByte(),
                't'.code.toByte(),
                ' '.code.toByte(),
                16, // 4 bytes: size of 'fmt ' chunk
                0,
                0,
                0,
                1, // format = 1
                0,
                CHANNEL_CONFIG.toByte(),
                0,
                (SAMPLING_RATE_IN_HZ and 0xff).toByte(),
                ((SAMPLING_RATE_IN_HZ shr 8) and 0xff).toByte(),
                ((SAMPLING_RATE_IN_HZ shr 16) and 0xff).toByte(),
                ((SAMPLING_RATE_IN_HZ shr 24) and 0xff).toByte(),
                (BYTE_RATE and 0xff).toByte(),
                ((BYTE_RATE shr 8) and 0xff).toByte(),
                ((BYTE_RATE shr 16) and 0xff).toByte(),
                ((BYTE_RATE shr 24) and 0xff).toByte(),
                1, // block align
                0,
                BIT_RATE.toByte(), // bits per sample
                0,
                'd'.code.toByte(),
                'a'.code.toByte(),
                't'.code.toByte(),
                'a'.code.toByte(),
                (audioSize and 0xff).toByte(),
                ((audioSize shr 8) and 0xff).toByte(),
                ((audioSize shr 16) and 0xff).toByte(),
                ((audioSize shr 24) and 0xff).toByte() //40-43 data size of rest
            )
            return header.toByteArray() + this
        }
    }

}