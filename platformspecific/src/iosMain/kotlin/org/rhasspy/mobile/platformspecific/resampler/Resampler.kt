package org.rhasspy.mobile.platformspecific.resampler

import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType

actual class Resampler actual constructor(
    inputChannelType: AudioFormatChannelType,
    inputEncodingType: AudioFormatEncodingType,
    inputSampleRateType: AudioFormatSampleRateType,
    outputChannelType: AudioFormatChannelType,
    outputEncodingType: AudioFormatEncodingType,
    outputSampleRateType: AudioFormatSampleRateType,
) {
    actual fun resample(inputData: ByteArray): ByteArray {
        //TODO #408
        return inputData
    }

    actual fun dispose() {
        //TODO #408
    }

}