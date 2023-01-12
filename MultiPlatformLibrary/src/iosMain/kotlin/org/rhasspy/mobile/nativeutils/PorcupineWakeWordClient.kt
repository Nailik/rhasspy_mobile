package org.rhasspy.mobile.nativeutils

import io.ktor.utils.io.core.*
import org.rhasspy.mobile.services.wakeword.PorcupineError
import org.rhasspy.mobile.settings.option.PorcupineLanguageOption
import org.rhasspy.mobile.settings.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.settings.porcupine.PorcupineDefaultKeyword


actual class PorcupineWakeWordClient actual constructor(
    wakeWordPorcupineAccessToken: String,
    wakeWordPorcupineKeywordDefaultOptions: Set<PorcupineDefaultKeyword>,
    wakeWordPorcupineKeywordCustomOptions: Set<PorcupineCustomKeyword>,
    wakeWordPorcupineLanguage: PorcupineLanguageOption,
    onKeywordDetected: (hotWord: String) -> Unit,
    onError: (PorcupineError) -> Unit
) : Closeable {
    override fun close() {
        TODO("Not yet implemented")
    }

    /**
     * create porcupine client
     */
    actual fun initialize(): PorcupineError? {
        TODO("Not yet implemented")
    }

    /**
     * start wake word detected
     */
    actual fun start(): PorcupineError? {
        TODO("Not yet implemented")
    }

    /**
     * stop wake word detected
     */
    actual fun stop() {
    }


}