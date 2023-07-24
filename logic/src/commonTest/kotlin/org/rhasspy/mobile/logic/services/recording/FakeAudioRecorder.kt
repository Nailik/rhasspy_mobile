package org.rhasspy.mobile.logic.services.recording

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder

class FakeAudioRecorder : IAudioRecorder {

    private val _maxVolume = MutableStateFlow(0f)
    override val output: Flow<ByteArray> = MutableSharedFlow()

    override val maxVolume: StateFlow<Float> = _maxVolume
    override val isRecording: StateFlow<Boolean> = MutableStateFlow(true)
    override val absoluteMaxVolume: Float = 32767.0f

    override fun startRecording(
        audioRecorderSampleRateType: AudioRecorderSampleRateType,
        audioRecorderChannelType: AudioRecorderChannelType,
        audioRecorderEncodingType: AudioRecorderEncodingType
    ) {
    }

    override fun stopRecording() {}

    suspend fun sendMaxVolume(volume: Float) {
        _maxVolume.emit(volume)
    }

}