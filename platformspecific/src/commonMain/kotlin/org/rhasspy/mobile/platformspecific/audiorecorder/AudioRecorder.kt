package org.rhasspy.mobile.platformspecific.audiorecorder

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType

expect class AudioRecorder(
    audioRecorderSampleRateType: AudioRecorderSampleRateType,
    audioRecorderChannelType: AudioRecorderChannelType,
    audioRecorderEncodingType: AudioRecorderEncodingType
) {

    /**
     * output data as flow
     */
    val output: Flow<ByteArray>

    /**
     * max volume since start recording
     */
    val maxVolume: StateFlow<Short>

    //state if currently recording
    val isRecording: StateFlow<Boolean>

    //maximum audio level that can happen
    val absoluteMaxVolume: Double

    /**
     * start recording
     */
    fun startRecording()

    /**
     * stop recording
     */
    fun stopRecording()

}