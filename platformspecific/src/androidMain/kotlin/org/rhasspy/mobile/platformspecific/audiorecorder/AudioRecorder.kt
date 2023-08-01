package org.rhasspy.mobile.platformspecific.audiorecorder

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.readOnly
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal actual class AudioRecorder : IAudioRecorder, KoinComponent {
    private val logger = Logger.withTag("AudioRecorder")

    /**
     * output data as flow
     */
    private val _output = MutableSharedFlow<ByteArray>()
    actual override val output = _output.readOnly

    /**
     * max volume since start recording
     */
    private val _maxVolume = MutableStateFlow(0f)
    actual override val maxVolume = _maxVolume.readOnly

    //state if currently recording
    private val _isRecording = MutableStateFlow(false)
    actual override val isRecording = _isRecording.readOnly

    //maximum audio level that can happen
    //https://developer.android.com/reference/android/media/AudioFormat#encoding
    actual override val absoluteMaxVolume = 32767.0f

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var recorder: AudioRecord? = null


    /**
     * start recording
     *
     * creates audio recorder if null
     */
    @SuppressLint("MissingPermission")
    actual override fun startRecording(
        audioRecorderChannelType: AudioFormatChannelType,
        audioRecorderEncodingType: AudioFormatEncodingType,
        audioRecorderSampleRateType: AudioFormatSampleRateType,
    ) {

        val tempBufferSize = AudioRecord.getMinBufferSize(
            audioRecorderSampleRateType.value,
            audioRecorderChannelType.value,
            audioRecorderEncodingType.value
        ) * 2

        logger.v { "startRecording" }

        if (ActivityCompat.checkSelfPermission(
                get<NativeApplication>() as Context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            logger.e { "missing recording permission" }
            return
        }

        try {
            if (recorder == null) {
                logger.v { "initializing recorder $tempBufferSize" }
                recorder = AudioRecord.Builder()
                    .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(audioRecorderSampleRateType.value)
                            .setChannelMask(audioRecorderChannelType.value)
                            .setEncoding(audioRecorderEncodingType.value)
                            .build()
                    )
                    .setBufferSizeInBytes(tempBufferSize)
                    .build()
            }

            _isRecording.value = true
            recorder?.startRecording()
            read(tempBufferSize)
        } catch (e: Exception) {
            _isRecording.value = false
            logger.e(e) { "native start recording error" }
        }
    }

    /**
     * stop recording
     */
    actual override fun stopRecording() {
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
     * reads from audio and emits buffer onto output
     */
    private fun read(bufferSize: Int) {
        var firstBuffer = true
        coroutineScope.launch {
            recorder?.also {
                while (it.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    try {
                        val byteArray = ByteArray(bufferSize)
                        if (it.read(byteArray, 0, byteArray.size) == bufferSize) {
                            updateMaxVolume(byteArray.clone())

                            //throw away first buffer to get rid of leading zeros
                            if (firstBuffer) {
                                firstBuffer = false
                            } else {
                                _output.emit(byteArray)
                            }
                        }
                    } catch (e: Exception) {
                        logger.e(e) { "recording exception" }
                    }
                }
            }
        }
    }

    private fun updateMaxVolume(data: ByteArray) {
        coroutineScope.launch {
            var max: Short = 0
            for (i in 0..data.size step 2) {
                if (i < data.size) {
                    val bb = ByteBuffer.wrap(data.copyOfRange(i, i + 2))
                    bb.order(ByteOrder.nativeOrder())
                    val short = bb.short

                    if (short > max) {
                        max = short
                    }
                }
            }
            _maxVolume.value = max.toFloat()
        }
    }

}