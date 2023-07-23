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
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.readOnly

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
        audioRecorderSampleRateType: AudioRecorderSampleRateType,
        audioRecorderChannelType: AudioRecorderChannelType,
        audioRecorderEncodingType: AudioRecorderEncodingType
    ) {

        val tempBufferSize = AudioRecord.getMinBufferSize(
            audioRecorderSampleRateType.value,
            audioRecorderChannelType.value,
            audioRecorderEncodingType.value
        ) * 2

        logger.v { "startRecording" }

        if (ActivityCompat.checkSelfPermission(get<NativeApplication>() as Context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            logger.e { "missing recording permission" }
            return
        }

        try {
            if (recorder == null) {
                logger.v { "initializing recorder" }
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
        coroutineScope.launch {
            recorder?.also {
                while (it.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    val byteArray = ByteArray(bufferSize)
                    if (it.read(byteArray, 0, byteArray.size) == bufferSize) {

                        /* coroutineScope.launch {
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
                             _maxVolume.value = max.toFloat()
                         }
 */
                        _output.emit(byteArray)
                    }
                }
            }
        }
    }

}