package org.rhasspy.mobile.platformspecific.resampler

import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType

actual class Resampler actual constructor(
    inputSampleRateType: AudioFormatSampleRateType,
    inputChannelType: AudioFormatChannelType,
    inputEncodingType: AudioFormatEncodingType,
    outputSampleRateType: AudioFormatSampleRateType,
    outputChannelType: AudioFormatChannelType,
    outputEncodingType: AudioFormatEncodingType
) {
    actual fun resample(inputData: ByteArray): ByteArray {
        //TODO("Not yet implemented")
        return inputData
    }

    actual fun dispose() {
        //TODO("Not yet implemented")
    }

}