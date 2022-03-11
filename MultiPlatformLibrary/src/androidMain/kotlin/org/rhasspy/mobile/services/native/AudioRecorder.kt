package org.rhasspy.mobile.services.native

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioRecord.RECORDSTATE_RECORDING
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
actual object AudioRecorder {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val recorder = AudioRecord.Builder()
        .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
        .setAudioFormat(
            AudioFormat.Builder()
                .setSampleRate(16000)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                .build()
        )
        .build()

    actual val output = MutableSharedFlow<ByteArray>()


    actual fun startRecording() {
        recorder.startRecording()
        read()
    }

    private fun read() {
        coroutineScope.launch {
            while (recorder.recordingState == RECORDSTATE_RECORDING) {
                val byteArray = ByteArray(recorder.bufferSizeInFrames)
                recorder.read(byteArray, 0, byteArray.size)
                output.emit(byteArray)
            }
        }
    }

    actual fun stopRecording() {
        recorder.stop()
    }
}