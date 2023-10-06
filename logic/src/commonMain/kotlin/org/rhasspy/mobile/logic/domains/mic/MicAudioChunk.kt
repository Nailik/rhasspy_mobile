package org.rhasspy.mobile.logic.domains.mic

import kotlinx.datetime.Instant
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType

internal class MicAudioChunk(
    val timeStamp: Instant,
    val sampleRate: AudioFormatSampleRateType,
    val encoding: AudioFormatEncodingType,
    val channel: AudioFormatChannelType,
    val data: ByteArray,
)