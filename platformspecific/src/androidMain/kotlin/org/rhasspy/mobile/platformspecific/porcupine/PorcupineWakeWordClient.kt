package org.rhasspy.mobile.platformspecific.porcupine

import ai.picovoice.porcupine.PorcupineException
import co.touchlab.kermit.Logger
import kotlinx.collections.immutable.ImmutableList
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
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
    private val wakeWordPorcupineKeywordDefaultOptions: ImmutableList<PorcupineDefaultKeyword>,
    private val wakeWordPorcupineKeywordCustomOptions: ImmutableList<PorcupineCustomKeyword>,
    private val wakeWordPorcupineLanguage: PorcupineLanguageOption,
    private val onKeywordDetected: (hotWord: String) -> Unit,
) : KoinComponent {

    private val logger = Logger.withTag("PorcupineWakeWordClient")

    //manager to stop start and reload porcupine
    private var porcupineClient: PorcupineClient? = null

    private val context = get<NativeApplication>()

    /**
     * start wake word detected
     *
     * start listening to wake words
     * requires internet to activate porcupine the very first time
     *
     * checks for audio permission
     * tries to start porcupine
     */
    actual fun start(): Exception? {
        if (porcupineClient == null) {
            return initialize()
        }

        return null
    }

    actual fun audioFrame(data: ByteArray) {
        porcupineClient?.audioFrame(data)
    }

    /**
     * deletes the porcupine manager
     */
    actual fun close() {
        porcupineClient?.close()
        porcupineClient = null
    }

    /**
     * create porcupine client
     */
    private fun initialize(): Exception? {
        return try {
            File(context.filesDir, "sounds").mkdirs()

            porcupineClient = PorcupineClient(
                wakeWordPorcupineAccessToken = wakeWordPorcupineAccessToken,
                wakeWordPorcupineKeywordDefaultOptions = wakeWordPorcupineKeywordDefaultOptions,
                wakeWordPorcupineKeywordCustomOptions = wakeWordPorcupineKeywordCustomOptions,
                wakeWordPorcupineLanguage = wakeWordPorcupineLanguage,
                onKeywordDetected = ::onKeywordDetected,
            )

            null//no error
        } catch (exception: PorcupineException) {
            return Exception(exception.localizedMessage)
        } catch (exception: Exception) {
            return exception
        }
    }


    /**
     * invoked when a WakeWord is detected, informs listening service
     */
    private fun onKeywordDetected(keywordIndex: Int) {
        logger.d { "invoke - keyword detected" }

        val allKeywords = wakeWordPorcupineKeywordDefaultOptions.filter { it.isEnabled }.map {
            it.option.name
        }.toMutableList().apply {
            addAll(wakeWordPorcupineKeywordCustomOptions.filter { it.isEnabled }.map {
                it.fileName
            }.toMutableList())
        }

        if (keywordIndex in 0..allKeywords.size) {
            onKeywordDetected(allKeywords[keywordIndex])
        } else if (keywordIndex > 0) {
            onKeywordDetected("UnknownIndex $keywordIndex")
        }
    }


}