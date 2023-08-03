package org.rhasspy.mobile.platformspecific.resampler

import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType

expect class Resampler(
    inputChannelType: AudioFormatChannelType,
    inputEncodingType: AudioFormatEncodingType,
    inputSampleRateType: AudioFormatSampleRateType,
    outputChannelType: AudioFormatChannelType,
    outputEncodingType: AudioFormatEncodingType,
    outputSampleRateType: AudioFormatSampleRateType,
) {

    fun resample(inputData: ByteArray): ByteArray

    fun dispose()

}