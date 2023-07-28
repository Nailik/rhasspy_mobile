package org.rhasspy.mobile.platformspecific.porcupine

import ai.picovoice.porcupine.Porcupine
import co.touchlab.kermit.Logger
import io.github.nailik.androidresampler.Resampler
import io.github.nailik.androidresampler.ResamplerConfiguration
import io.github.nailik.androidresampler.data.ResamplerChannel
import io.github.nailik.androidresampler.data.ResamplerQuality
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
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
import java.nio.ByteOrder.LITTLE_ENDIAN

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

    private val logger = Logger.withTag("PorcupineCustomClient")

    private val porcupine = Porcupine.Builder()
        .setAccessKey(wakeWordPorcupineAccessToken)
        .setKeywordPaths(getKeywordPaths())
        .setSensitivities(getSensitivities())
        .setModelPath(copyModelFile())
        .build(context)

    private val audioRecorder by inject<IAudioRecorder>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var collection: Job? = null
    private var resampler: Resampler? = null
    private var isStarted = false

    private fun AudioRecorderChannelType.toResamplerChannel(): ResamplerChannel {
        return when (this) {
            AudioRecorderChannelType.Default -> ResamplerChannel.MONO
            AudioRecorderChannelType.Mono    -> ResamplerChannel.MONO
            AudioRecorderChannelType.Stereo  -> ResamplerChannel.STEREO
        }
    }

    override fun start() {
        if (isStarted) return

        isStarted = true
        resampler = Resampler(
            ResamplerConfiguration(
                quality = ResamplerQuality.BEST,
                inputChannel = audioRecorderChannelType.toResamplerChannel(),
                inputSampleRate = audioRecorderSampleRateType.value,
                outputChannel = ResamplerChannel.MONO,
                outputSampleRate = porcupine.sampleRate
            )
        )
        logger.d { "porcupine.getSampleRate() ${porcupine.sampleRate}" }

        collection = coroutineScope.launch {

            var oldData = ShortArray(0)
            try {
                audioRecorder.output.collectLatest { data ->
                    if (resampler == null) {
                        logger.e("resampler is null")
                        return@collectLatest
                    }
                    try {
                        //    logger.d { "resample ${data.size}" }
                        //resample the data
                        val toResample: ByteArray = data
                        val resampled = resampler!!.resample(toResample)
                        //convert ByteArray to ShortArray as required by porcupine and add to data
                        var currentRecording = oldData + byteArrayToShortArray(resampled)

                        //send to porcupine
                        while (currentRecording.size >= 512) {
                            //get a sized chunk
                            val chunk = currentRecording.take(512).toShortArray()
                            //cut remaining data
                            currentRecording = currentRecording.takeLast(currentRecording.size - 512).toShortArray()

                            val keywordIndex = porcupine.process(chunk)
                            if (keywordIndex != -1) {
                                onKeywordDetected(keywordIndex)
                            }
                        }

                        oldData = currentRecording
                    } catch (e: Exception) {
                        logger.e("", e)
                    }
                }
            } catch (e: Exception) {
                logger.e("", e)
            }
        }

        audioRecorder.startRecording(
            audioRecorderChannelType = audioRecorderChannelType,
            audioRecorderEncodingType = audioRecorderEncodingType,
            audioRecorderSampleRateType = audioRecorderSampleRateType
        )
    }


    private fun byteArrayToShortArray(byteArray: ByteArray): ShortArray {
        val shorts = ShortArray(byteArray.size / 2)
        ByteBuffer.wrap(byteArray).order(LITTLE_ENDIAN).asShortBuffer().get(shorts)
        return shorts
    }

    override fun stop() {
        isStarted = false
        resampler?.dispose()
        collection?.cancel()
        audioRecorder.stopRecording()
    }

    override fun close() {
        isStarted = false
        resampler?.dispose()
        collection?.cancel()
        porcupine.delete()
    }

}