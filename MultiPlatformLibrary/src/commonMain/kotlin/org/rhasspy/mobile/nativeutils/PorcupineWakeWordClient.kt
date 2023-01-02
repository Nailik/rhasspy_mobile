package org.rhasspy.mobile.nativeutils

import io.ktor.utils.io.core.*
import org.rhasspy.mobile.services.wakeword.PorcupineError
import org.rhasspy.mobile.settings.option.PorcupineLanguageOption
import org.rhasspy.mobile.settings.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.settings.porcupine.PorcupineDefaultKeyword

/**
 * Listens to WakeWord with Porcupine
 *
 * start listening to wake words
 * requires internet to activate porcupine the very first time
 */
expect class PorcupineWakeWordClient(
    wakeWordPorcupineAccessToken: String,
    wakeWordPorcupineKeywordDefaultOptions: Set<PorcupineDefaultKeyword>,
    wakeWordPorcupineKeywordCustomOptions: Set<PorcupineCustomKeyword>,
    wakeWordPorcupineLanguage: PorcupineLanguageOption,
    onKeywordDetected: (hotWord: String) -> Unit,
    onError: (PorcupineError) -> Unit
) : Closeable {

    /**
     * create porcupine client
     */
    fun initialize(): PorcupineError?

    /**
     * start wake word detected
     */
    fun start(): PorcupineError?

    /**
     * stop wake word detected
     */
    fun stop()

}