package org.rhasspy.mobile.platformspecific.porcupine

import android.media.MediaCodec
import android.media.MediaCodec.BufferInfo
import android.media.MediaCodecList
import android.media.MediaCodecList.ALL_CODECS
import android.media.MediaFormat
import co.touchlab.kermit.Logger
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType
import kotlin.math.min


class PorcupineAudioConverter(
    audioRecorderSampleRateType: AudioRecorderSampleRateType,
    audioRecorderChannelType: AudioRecorderChannelType,
    audioRecorderEncodingType: AudioRecorderEncodingType,
) {

    lateinit var inputCodec: MediaCodec
    lateinit var outputCodec: MediaCodec

    init {
        val codecs =
            MediaCodecList(ALL_CODECS).codecInfos
        println("codecs $codecs")
        val inputFormat = MediaFormat.createAudioFormat(
            "audio/raw",
            audioRecorderSampleRateType.value,
            audioRecorderChannelType.count
        )
        inputFormat.setInteger(MediaFormat.KEY_PCM_ENCODING, audioRecorderEncodingType.value)
        val inputCodec = MediaCodec.createDecoderByType("audio/raw")
        inputCodec.configure(inputFormat, null, null, 0)
        inputCodec.start()

        val outputFormat = MediaFormat.createAudioFormat(
            "audio/raw",
            AudioRecorderSampleRateType.SR12000.value,
            AudioRecorderChannelType.Mono.count
        )
        outputFormat.setInteger(
            MediaFormat.KEY_PCM_ENCODING,
            AudioRecorderEncodingType.PCM16Bit.value
        )
        val outputCodec = MediaCodec.createEncoderByType("audio/mp4a-latm")
        outputCodec.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        outputCodec.start()
    }

    fun convert(inputData: ByteArray): ByteArray {

        val inputBuffer = inputCodec.getInputBuffer(0)
        inputBuffer!!.put(inputData)
        inputCodec.queueInputBuffer(0, 0, inputData.size, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)

        val bufferInfo = BufferInfo()
        inputCodec.dequeueOutputBuffer(bufferInfo, 0)
        val decodedBuffer = inputCodec.getOutputBuffer(0)

        Logger.withTag("PorcupineAudioConverter").d { "${inputData.size}" }
        val inputBufferSize = inputData.size
        val outputBufferSize = 1024
        val outputData = ByteArray(outputBufferSize)

        var inputIndex = 0
        var outputIndex = 0

        while (inputIndex < inputBufferSize) {
            val inputBufferIndex = inputCodec.dequeueInputBuffer(10000)
            if (inputBufferIndex >= 0) {
                val inputBuffer = inputCodec.getInputBuffer(inputBufferIndex)!!
                val remaining = min(inputBufferSize - inputIndex, inputBuffer.remaining())
                inputBuffer.put(inputData, inputIndex, remaining)
                inputIndex += remaining
                inputCodec.queueInputBuffer(inputBufferIndex, 0, remaining, 0, 0)
            }

            val outputBufferIndex = inputCodec.dequeueOutputBuffer(BufferInfo(), 10000)
            if (outputBufferIndex >= 0) {
                val outputBuffer = inputCodec.getOutputBuffer(outputBufferIndex)!!
                if (outputBuffer.remaining() > outputBufferSize - outputIndex) {
                    outputBuffer.limit(outputIndex + outputBufferSize)
                }
                outputBuffer.get(outputData, outputIndex, outputBuffer.remaining())
                outputIndex += outputBuffer.remaining()
                inputCodec.releaseOutputBuffer(outputBufferIndex, false)
            }
        }
        Logger.withTag("PorcupineAudioConverter").d { "outputData" }

        return outputData

    }

    fun stop() {
        inputCodec.stop()
        inputCodec.release()

        outputCodec.stop()
        outputCodec.release()
    }


}