package org.rhasspy.mobile.platformspecific.audiorecorder

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType

interface IAudioRecorder {

    val output: Flow<ByteArray> //TODO maybe hold byte and short array for convenience
    val maxVolume: StateFlow<Float>
    val isRecording: StateFlow<Boolean>
    val absoluteMaxVolume: Float
    fun startRecording(
        audioRecorderSampleRateType: AudioRecorderSampleRateType,
        audioRecorderChannelType: AudioRecorderChannelType,
        audioRecorderEncodingType: AudioRecorderEncodingType
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
        audioRecorderSampleRateType: AudioRecorderSampleRateType,
        audioRecorderChannelType: AudioRecorderChannelType,
        audioRecorderEncodingType: AudioRecorderEncodingType
    )

    /**
     * stop recording
     */
    override fun stopRecording()

}