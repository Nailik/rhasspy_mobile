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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.Application

actual object AudioRecorder {
    private val logger = Logger.withTag("AudioRecorder")

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var recorder: AudioRecord? = null

    actual val output = MutableSharedFlow<List<Byte>>()


    actual fun startRecording() {
        logger.v { "startRecording" }

        if (recorder == null) {
            logger.v { "initializing recorder" }

            if (ActivityCompat.checkSelfPermission(Application.Instance, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                logger.e { "missing recording permission" }
                return
            }

            recorder = AudioRecord.Builder()
                .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(16000)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                        .build()
                )
                .setBufferSizeInBytes(8000)
                .build()
        }

        recorder?.startRecording()
        read()
    }

    private fun read() {
        coroutineScope.launch {
            recorder?.also {
                while (it.recordingState == RECORDSTATE_RECORDING) {
                    val byteArray = ByteArray(it.bufferSizeInFrames)
                    it.read(byteArray, 0, byteArray.size)

                    output.emit(byteArray.toList())
                }
            }
        }
    }


    actual fun stopRecording() {
        logger.v { "stopRecording" }
        recorder?.stop()
    }
}