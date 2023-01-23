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
    private val _output = MutableSharedFlow<List<Byte>>()
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

    companion object {
        private const val SAMPLING_RATE_IN_HZ = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val BUFFER_SIZE_FACTOR = 2
        private val BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLING_RATE_IN_HZ,
            CHANNEL_CONFIG, AUDIO_FORMAT
        ) * BUFFER_SIZE_FACTOR
    }

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

                    _output.emit(byteArray.toList())
                }
            }
        }
    }

}