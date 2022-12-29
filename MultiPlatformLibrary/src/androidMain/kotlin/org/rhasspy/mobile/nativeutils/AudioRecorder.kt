package org.rhasspy.mobile.nativeutils

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioRecord.RECORDSTATE_RECORDING
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.readOnly
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual object AudioRecorder {
    private val logger = Logger.withTag("AudioRecorder")

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var recorder: AudioRecord? = null

    private val _output = MutableStateFlow<List<Byte>>(listOf())
    private val _maxVolume = MutableStateFlow<Short>(0)
    private val _isRecording = MutableStateFlow(false)

    actual val output = _output.readOnly
    actual val maxVolume = _maxVolume.readOnly
    actual val isRecording = _isRecording.readOnly

    //https://developer.android.com/reference/android/media/AudioFormat#encoding
    actual val absoluteMaxVolume = 32767.0

    private const val SAMPLING_RATE_IN_HZ = 16000
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

    private val BUFFER_SIZE_FACTOR = 2

    private val BUFFER_SIZE: Int = AudioRecord.getMinBufferSize(
        SAMPLING_RATE_IN_HZ,
        CHANNEL_CONFIG, AUDIO_FORMAT
    ) * BUFFER_SIZE_FACTOR

    actual fun startRecording() {
        logger.v { "startRecording" }

        if (recorder == null) {
            logger.v { "initializing recorder" }

            if (ActivityCompat.checkSelfPermission(
                    Application.Instance,
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

        try {
            recorder?.startRecording()
            read()
        } catch (e: Exception) {

        }
    }

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

                    _output.value = byteArray.toList()
                }
            }
        }
    }


    actual fun stopRecording() {
        _isRecording.value = false
        logger.v { "stopRecording" }
        recorder?.stop()  //TODO simplify do not always create new recorder
        recorder?.release()
        recorder = null
    }
}