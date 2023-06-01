package org.rhasspy.mobile.platformspecific.audiorecorder

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType

actual class AudioRecorder {

    /**
     * max volume since start recording
     */
    actual val maxVolume: StateFlow<Float>
        get() = MutableStateFlow(Float.MAX_VALUE) //TODO("Not yet implemented")
    actual val isRecording: StateFlow<Boolean>
        get() = MutableStateFlow(true) //TODO("Not yet implemented")
    actual val absoluteMaxVolume: Float
        get() = 0f //TODO("Not yet implemented")

    /**
     * start recording
     */
    actual fun startRecording(
        audioRecorderSampleRateType: AudioRecorderSampleRateType,
        audioRecorderChannelType: AudioRecorderChannelType,
        audioRecorderEncodingType: AudioRecorderEncodingType
    ) {
        //TODO("Not yet implemented")
    }

    /**
     * stop recording
     */
    actual fun stopRecording() {
        //TODO("Not yet implemented")
    }

    /**
     * output data as flow
     */
    actual val output: Flow<ByteArray>
        get() = MutableStateFlow(ByteArray(0)) //TODO("Not yet implemented")

}