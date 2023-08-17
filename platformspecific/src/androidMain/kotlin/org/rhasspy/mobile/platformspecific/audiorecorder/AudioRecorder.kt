package org.rhasspy.mobile.platformspecific.audiorecorder

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.*
import android.media.AudioRecord.RECORDSTATE_RECORDING
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.resampler.Resampler
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal actual class AudioRecorder : IAudioRecorder, KoinComponent {
    private val logger = Logger.withTag("AudioRecorder")

    private val nativeApplication by inject<NativeApplication>()
    private val audioManager = nativeApplication.getSystemService<AudioManager>()

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

    private var shouldRecord = false

    //state if currently recording
    private var _isRecording = MutableStateFlow(false)
    actual override val isRecording = _isRecording.readOnly

    //maximum audio level that can happen
    //https://developer.android.com/reference/android/media/AudioFormat#encoding
    actual override val absoluteMaxVolume = 32767.0f

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var recorder: AudioRecord? = null
    private var resampler: Resampler? = null

    private val audioPlaybackCallback =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> AudioManagerCallback(
                callback = { isPlaying ->
                    if (isPlaying) {
                        pauseRecording()
                    } else {
                        resumeRecording()
                    }
                },
                audioManager = audioManager
            )

            else                                           -> AudioManagerCallbackLegacy()
        }

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
        audioRecorderOutputChannelType: AudioFormatChannelType,
        audioRecorderOutputEncodingType: AudioFormatEncodingType,
        audioRecorderOutputSampleRateType: AudioFormatSampleRateType,
        isAutoPauseOnMediaPlayback: Boolean,
    ) {
        shouldRecord = true
        if (_isRecording.value) return
        try {
            logger.v { "startRecording" }

            if (ActivityCompat.checkSelfPermission(
                    get<NativeApplication>() as Context,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                logger.e { "missing recording permission" }
                return
            }

            val tempBufferSize = AudioRecord.getMinBufferSize(
                audioRecorderSampleRateType.value,
                audioRecorderChannelType.value,
                audioRecorderEncodingType.value
            ) * 2

            resampler?.dispose()
            resampler = createResampler(
                audioRecorderChannelType = audioRecorderChannelType,
                audioRecorderEncodingType = audioRecorderEncodingType,
                audioRecorderSampleRateType = audioRecorderSampleRateType,
                audioRecorderOutputChannelType = audioRecorderOutputChannelType,
                audioRecorderOutputEncodingType = audioRecorderOutputEncodingType,
                audioRecorderOutputSampleRateType = audioRecorderOutputSampleRateType,
            )

            logger.v { "initializing recorder $tempBufferSize" }
            recorder?.release()
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

            _isRecording.value = true
            recorder?.startRecording()
            read(tempBufferSize)

            if (isAutoPauseOnMediaPlayback) {
                audioPlaybackCallback.register()
            }

        } catch (e: Exception) {
            _isRecording.value = false
            logger.e(e) { "native start recording error" }
        }
    }

    private fun pauseRecording() {
        logger.v { "pauseRecording ${recorder?.recordingState}" }
        _isRecording.value = false
        try {
            if (recorder?.recordingState == RECORDSTATE_RECORDING) {
                recorder?.stop()
            }
        } catch (e: Exception) {
            logger.a(e) { "pauseRecording" }
        }
    }

    private fun resumeRecording() {
        logger.v { "resumeRecording" }
        if (shouldRecord) {
            _isRecording.value = true
            recorder?.startRecording()
        }
    }

    /**shouldRecord
     * stop recording
     */
    actual override fun stopRecording() {
        audioPlaybackCallback.unregister()

        shouldRecord = false
        logger.v { "stopRecording ${recorder?.recordingState}" }
        if (_isRecording.value) {
            _isRecording.value = false
            try {
                if (recorder?.recordingState == RECORDSTATE_RECORDING) {
                    recorder?.stop()
                }
            } catch (e: Exception) {
                logger.a(e) { "pauseRecording" }
            }
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
                while (shouldRecord) {
                    try {
                        if (it.recordingState == RECORDSTATE_RECORDING) {
                            val byteArray = ByteArray(bufferSize)
                            if (it.read(byteArray, 0, byteArray.size) == bufferSize) {
                                updateMaxVolume(byteArray.clone())

                                //throw away first buffer to get rid of leading zeros
                                if (firstBuffer) {
                                    firstBuffer = false
                                } else {
                                    val data = byteArray.clone()
                                    _output.emit(resampler?.resample(data) ?: data)
                                }
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

    /**
     * get resampler or create new one if necessary
     */
    private fun createResampler(
        audioRecorderChannelType: AudioFormatChannelType,
        audioRecorderEncodingType: AudioFormatEncodingType,
        audioRecorderSampleRateType: AudioFormatSampleRateType,
        audioRecorderOutputChannelType: AudioFormatChannelType,
        audioRecorderOutputEncodingType: AudioFormatEncodingType,
        audioRecorderOutputSampleRateType: AudioFormatSampleRateType,
    ): Resampler {
        return Resampler(
            inputChannelType = audioRecorderChannelType,
            inputEncodingType = audioRecorderEncodingType,
            inputSampleRateType = audioRecorderSampleRateType,
            outputChannelType = audioRecorderOutputChannelType,
            outputEncodingType = audioRecorderOutputEncodingType,
            outputSampleRateType = audioRecorderOutputSampleRateType,
        )
    }

}