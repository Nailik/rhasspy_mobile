package org.rhasspy.mobile.platformspecific.audiorecorder

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType

interface IAudioRecorder {

    val output: Flow<ByteArray>
    val maxVolume: StateFlow<Float>
    val isRecording: StateFlow<Boolean>
    val absoluteMaxVolume: Float

    fun startRecording(
        audioRecorderChannelType: AudioFormatChannelType,
        audioRecorderEncodingType: AudioFormatEncodingType,
        audioRecorderSampleRateType: AudioFormatSampleRateType,
        audioRecorderOutputChannelType: AudioFormatChannelType,
        audioRecorderOutputEncodingType: AudioFormatEncodingType,
        audioRecorderOutputSampleRateType: AudioFormatSampleRateType,
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
        audioRecorderChannelType: AudioFormatChannelType,
        audioRecorderEncodingType: AudioFormatEncodingType,
        audioRecorderSampleRateType: AudioFormatSampleRateType,
        audioRecorderOutputChannelType: AudioFormatChannelType,
        audioRecorderOutputEncodingType: AudioFormatEncodingType,
        audioRecorderOutputSampleRateType: AudioFormatSampleRateType,
        isAutoPauseOnMediaPlayback: Boolean,
    )

    /**
     * stop recording
     */
    override fun stopRecording()

}