package org.rhasspy.mobile.platformspecific.audiorecorder

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.audiorecorder.AudioSourceType

interface IAudioRecorder {

    val output: Flow<ByteArray>
    val maxVolume: StateFlow<Float>
    val isRecording: StateFlow<Boolean>
    val absoluteMaxVolume: Float

    fun startRecording(
        audioRecorderSourceType: AudioSourceType,
        audioRecorderChannelType: AudioFormatChannelType,
        audioRecorderEncodingType: AudioFormatEncodingType,
        audioRecorderSampleRateType: AudioFormatSampleRateType,
        audioRecorderOutputChannelType: AudioFormatChannelType,
        audioRecorderOutputEncodingType: AudioFormatEncodingType,
        audioRecorderOutputSampleRateType: AudioFormatSampleRateType,
        isUseLoudnessEnhancer: Boolean,
        gainControl: Int,
        isAutoPauseOnMediaPlayback: Boolean,
    )

    fun stopRecording()

}

internal expect class AudioRecorder() : IAudioRecorder {

    /**
     * output data as flow
     */
    override val output: Flow<ByteArray>

    /**
     * max volume since start recording
     */
    override val maxVolume: StateFlow<Float>

    //state if currently recording
    override val isRecording: StateFlow<Boolean>

    //maximum audio level that can appear
    override val absoluteMaxVolume: Float

    /**
     * start recording
     */
    override fun startRecording(
        audioRecorderSourceType: AudioSourceType,
        audioRecorderChannelType: AudioFormatChannelType,
        audioRecorderEncodingType: AudioFormatEncodingType,
        audioRecorderSampleRateType: AudioFormatSampleRateType,
        audioRecorderOutputChannelType: AudioFormatChannelType,
        audioRecorderOutputEncodingType: AudioFormatEncodingType,
        audioRecorderOutputSampleRateType: AudioFormatSampleRateType,
        isUseLoudnessEnhancer: Boolean,
        gainControl: Int,
        isAutoPauseOnMediaPlayback: Boolean,
    )

    /**
     * stop recording
     */
    override fun stopRecording()

}