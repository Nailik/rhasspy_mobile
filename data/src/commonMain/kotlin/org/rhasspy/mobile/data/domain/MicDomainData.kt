package org.rhasspy.mobile.data.domain

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType

@Serializable
data class MicDomainData(
    val audioInputChannel: AudioFormatChannelType,
    val audioInputEncoding: AudioFormatEncodingType,
    val audioInputSampleRate: AudioFormatSampleRateType,
    val audioOutputChannel: AudioFormatChannelType,
    val audioOutputEncoding: AudioFormatEncodingType,
    val audioOutputSampleRate: AudioFormatSampleRateType,
    val isUseLoudnessEnhancer: Boolean,
    val gainControl: Int,
    val isPauseRecordingOnMediaPlayback: Boolean,
)