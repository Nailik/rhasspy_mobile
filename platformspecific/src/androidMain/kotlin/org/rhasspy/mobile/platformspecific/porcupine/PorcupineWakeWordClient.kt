package org.rhasspy.mobile.platformspecific.porcupine

import ai.picovoice.porcupine.PorcupineException
import co.touchlab.kermit.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import java.io.File

/**
 * Listens to WakeWord with Porcupine
 *
 * checks for audio permission
 */
actual class PorcupineWakeWordClient actual constructor(
    private val wakeWordPorcupineAccessToken: String,
    private val wakeWordPorcupineKeywordDefaultOptions: List<PorcupineDefaultKeyword>,
    private val wakeWordPorcupineKeywordCustomOptions: List<PorcupineCustomKeyword>,
    private val wakeWordPorcupineLanguage: PorcupineLanguageOption,
) : KoinComponent {

    private val logger = Logger.withTag("PorcupineWakeWordClient")

    //manager to stop start and reload porcupine
    private var porcupineClient: PorcupineClient? = null

    private val context = get<NativeApplication>()

    /**
     * create porcupine client
     *
     * requires internet to activate porcupine the very first time
     */
    actual fun initialize(): Exception? {
        return try {
            File(context.filesDir, "sounds").mkdirs()

            porcupineClient = PorcupineClient(
                wakeWordPorcupineAccessToken = wakeWordPorcupineAccessToken,
                wakeWordPorcupineKeywordDefaultOptions = wakeWordPorcupineKeywordDefaultOptions,
                wakeWordPorcupineKeywordCustomOptions = wakeWordPorcupineKeywordCustomOptions,
                wakeWordPorcupineLanguage = wakeWordPorcupineLanguage,
            )

            null//no error
        } catch (exception: PorcupineException) {
            return Exception(exception.localizedMessage)
        } catch (exception: Exception) {
            return exception
        }
    }

    actual fun audioFrame(
        sampleRate: AudioFormatSampleRateType,
        encoding: AudioFormatEncodingType,
        channel: AudioFormatChannelType,
        data: ByteArray,
    ): String? {
        val keywordIndex = porcupineClient?.audioFrame(data)

        if (keywordIndex == null || keywordIndex < 0) return null

        logger.d { "invoke - keyword detected" }

        val allKeywords = wakeWordPorcupineKeywordDefaultOptions.filter { it.isEnabled }.map {
            it.option.name
        }.toMutableList().apply {
            addAll(wakeWordPorcupineKeywordCustomOptions.filter { it.isEnabled }.map {
                it.fileName
            }.toMutableList())
        }

        return if (keywordIndex in 0..allKeywords.size) {
            allKeywords[keywordIndex]
        } else {
            "UnknownKeyword"
        }
    }

    /**
     * deletes the porcupine manager
     */
    actual fun close() {
        porcupineClient?.close()
        porcupineClient = null
    }

}