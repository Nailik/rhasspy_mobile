package org.rhasspy.mobile.platformspecific.resampler

import co.touchlab.kermit.Logger
import io.github.nailik.androidresampler.Resampler
import io.github.nailik.androidresampler.ResamplerConfiguration
import io.github.nailik.androidresampler.data.ResamplerChannel
import io.github.nailik.androidresampler.data.ResamplerQuality
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType

actual class Resampler actual constructor(
    private val inputChannelType: AudioFormatChannelType,
    inputEncodingType: AudioFormatEncodingType,
    private val inputSampleRateType: AudioFormatSampleRateType,
    private val outputChannelType: AudioFormatChannelType,
    outputEncodingType: AudioFormatEncodingType,
    private val outputSampleRateType: AudioFormatSampleRateType,
) {
    private val logger = Logger.withTag("Resampler")

    private var resampler: Resampler? = null

    private fun getResampler(): Resampler? {
        if (inputSampleRateType == outputSampleRateType && inputChannelType == outputChannelType) {
            return null
        }

        if (resampler != null) return resampler

        //TODO #408
        resampler = Resampler(
            ResamplerConfiguration(
                quality = ResamplerQuality.BEST,
                inputChannel = inputChannelType.toResamplerChannel(),
                inputSampleRate = inputSampleRateType.value,
                outputChannel = outputChannelType.toResamplerChannel(),
                outputSampleRate = outputSampleRateType.value
            )
        )

        return resampler
    }

    actual fun resample(inputData: ByteArray): ByteArray {
        return getResampler()?.resample(inputData) ?: inputData
    }

    actual fun dispose() {
        resampler?.dispose()
        resampler = null
    }

    private fun AudioFormatChannelType.toResamplerChannel(): ResamplerChannel {
        return when (this) {
            AudioFormatChannelType.Mono   -> ResamplerChannel.MONO
            AudioFormatChannelType.Stereo -> ResamplerChannel.STEREO
        }
    }

}