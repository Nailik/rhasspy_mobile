package org.rhasspy.mobile.platformspecific.audiorecorder

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType

internal actual class AudioRecorder : IAudioRecorder {

    /**
     * max volume since start recording
     */
    actual override val maxVolume: StateFlow<Float>
        get() = MutableStateFlow(Float.MAX_VALUE) //TODO("Not yet implemented")
    actual override val isRecording: StateFlow<Boolean>
        get() = MutableStateFlow(true) //TODO("Not yet implemented")
    actual override val absoluteMaxVolume: Float
        get() = 0f //TODO("Not yet implemented")

    /**
     * start recording
     */
    actual override fun startRecording(
        audioRecorderChannelType: AudioFormatChannelType,
        audioRecorderEncodingType: AudioFormatEncodingType,
        audioRecorderSampleRateType: AudioFormatSampleRateType,
        audioRecorderOutputChannelType: AudioFormatChannelType,
        audioRecorderOutputEncodingType: AudioFormatEncodingType,
        audioRecorderOutputSampleRateType: AudioFormatSampleRateType,
        isAutoPauseOnMediaPlayback: Boolean,
    ) {
        //TODO("Not yet implemented")
    }

    /**
     * stop recording
     */
    actual override fun stopRecording() {
        //TODO("Not yet implemented")
    }

    /**
     * output data as flow
     */
    actual override val output: Flow<ByteArray>
        get() = MutableStateFlow(ByteArray(0)) //TODO("Not yet implemented")

}