package org.rhasspy.mobile.platformspecific.porcupine

import ai.picovoice.porcupine.Porcupine
import co.touchlab.kermit.Logger
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PorcupineCustomClient(
    private val audioRecorderSampleRateType: AudioRecorderSampleRateType,
    private val audioRecorderChannelType: AudioRecorderChannelType,
    private val audioRecorderEncodingType: AudioRecorderEncodingType,
    override val wakeWordPorcupineAccessToken: String,
    override val wakeWordPorcupineKeywordDefaultOptions: ImmutableList<PorcupineDefaultKeyword>,
    override val wakeWordPorcupineKeywordCustomOptions: ImmutableList<PorcupineCustomKeyword>,
    override val wakeWordPorcupineLanguage: PorcupineLanguageOption,
    override val onKeywordDetected: (keywordIndex: Int) -> Unit,
    override val onError: (Exception) -> Unit
) : IPorcupineClient() {

    private val porcupine = Porcupine.Builder()
        .setAccessKey(wakeWordPorcupineAccessToken)
        .setKeywordPaths(getKeywordPaths())
        .setSensitivities(getSensitivities())
        .setModelPath(copyModelFile())
        .build(context)

    private val audioRecorder by inject<IAudioRecorder>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var collection: Job? = null

    override fun start() {
        Logger.withTag("PorcupineCustomClient").d { "porcupine.getSampleRate() ${porcupine.sampleRate}" }

        collection = coroutineScope.launch {
            var leftOver = ShortArray(0)
            try {
                audioRecorder.output.collect { data ->
                    try {
                        val resampled = linearInterpolation(data, audioRecorderSampleRateType.value, 16000)
                        // val converted = convert32BitTo16Bit2(data)
                        val shortArray = byteArrayToShortArray(resampled)

                        var allData = leftOver + shortArray
                        leftOver = ShortArray(0)

                        while (allData.size >= 512) {
                            val chunk = allData.take(512).toShortArray()
                            allData = allData.takeLast(allData.size - 512).toShortArray()
                            Logger.withTag("PorcupineCustomClient").d { "${chunk.size} ${data.size} - ${shortArray.size}" }
                            val keywordIndex = porcupine.process(chunk)
                            if (keywordIndex != -1) {
                                onKeywordDetected(keywordIndex)
                            }
                        }
                        leftOver = allData


                    } catch (e: Exception) {
                        Logger.withTag("PorcupineCustomClient").e("", e)
                    }
                }
            } catch (e: Exception) {
                Logger.withTag("PorcupineCustomClient").e("", e)
            }
        }
        audioRecorder.startRecording(
            audioRecorderChannelType = audioRecorderChannelType,
            audioRecorderEncodingType = audioRecorderEncodingType,
            audioRecorderSampleRateType = audioRecorderSampleRateType
        )
    }

    override fun stop() {
        collection?.cancel()
        audioRecorder.stopRecording()
    }

    override fun close() {
        collection?.cancel()
        porcupine.delete()
    }

    fun byteArrayToShortArray(inputData: ByteArray): ShortArray {
        val shortBuffer = ByteBuffer.wrap(inputData)
            .order(ByteOrder.LITTLE_ENDIAN)
            .asShortBuffer()

        val shortArray = ShortArray(shortBuffer.remaining())
        shortBuffer.get(shortArray)

        return shortArray
    }

    fun linearInterpolation(inputAudio: ByteArray, inputSampleRate: Int, targetSampleRate: Int): ByteArray {
        //how much has this to be converted
        val conversionFactor = inputSampleRate.toDouble() / targetSampleRate.toDouble()

        val targetSize = (inputAudio.size.toDouble() * conversionFactor).toInt()
        val output = ByteArray(targetSize)


        for (targetIndex in 0 until targetSize) {
            val inputIndex = (targetIndex * conversionFactor).toInt()
            val nextInputIndex = inputIndex + 1

            if (nextInputIndex >= inputAudio.size) {
                output[targetIndex] = inputAudio[inputIndex]
            } else {
                val fractionalPart = targetIndex * conversionFactor - inputIndex
                val sample1 = inputAudio[inputIndex].toInt()
                val sample2 = inputAudio[nextInputIndex].toInt()
                val interpolatedSample = (sample1 * (1 - fractionalPart) + sample2 * fractionalPart).toInt()
                output[targetIndex] = interpolatedSample.toByte()
            }
        }

        return output
    }


    //Works!
    fun convert32BitTo16Bit2(byteArray32: ByteArray): ByteArray {
        val byteArray16 = ByteArray(byteArray32.size / 2)

        for (i in byteArray16.indices) {
            val index32 = i * 2
            val value16 = ((byteArray32[index32].toInt() and 0xFF) shl 8) or (byteArray32[index32 + 1].toInt() and 0xFF)
            byteArray16[i] = value16.toByte()
        }

        return byteArray16
    }

    fun convert8BitTo16Bit3(input: ByteArray): ByteArray {
        val output = ByteArray(input.size * 2)
        var outputIndex = 0

        for (i in input.indices step 2) {
            val sample = input[i].toInt() and 0xFF or (input[i + 1].toInt() and 0xFF shl 8)
            output[outputIndex++] = (sample and 0xFF).toByte()
            output[outputIndex++] = (sample shr 8 and 0xFF).toByte()
        }

        return output
    }

    fun convert8BitTo16Bit(byteArray8: ByteArray): ByteArray {
        val byteArray16 = ByteArray(byteArray8.size * 2)

        for (i in byteArray8.indices) {
            byteArray16[i * 2] = byteArray8[i] // Most significant byte (MSB)
            byteArray16[i * 2 + 1] = 0 // Least significant byte (LSB)
        }

        return byteArray16
    }

    fun convert32BitTo16Bit(inputData: ByteArray): ByteArray {
        val inputSamples = inputData.size / 4 // 32-bit PCM has 4 bytes per sample
        val outputData = ByteArray(inputSamples * 2) // 16-bit PCM has 2 bytes per sample

        var inputIndex = 0
        var outputIndex = 0

        while (inputIndex < inputData.size) {
            val sample32Bit = ((inputData[inputIndex + 3].toInt() and 0xFF) shl 24) or
                    ((inputData[inputIndex + 2].toInt() and 0xFF) shl 16) or
                    ((inputData[inputIndex + 1].toInt() and 0xFF) shl 8) or
                    (inputData[inputIndex].toInt() and 0xFF)

            val sample16Bit = (sample32Bit shr 16)

            outputData[outputIndex + 1] = (sample16Bit shr 8).toByte()
            outputData[outputIndex] = (sample16Bit and 0xFF).toByte()

            inputIndex += 4
            outputIndex += 2
        }

        return outputData
    }


}