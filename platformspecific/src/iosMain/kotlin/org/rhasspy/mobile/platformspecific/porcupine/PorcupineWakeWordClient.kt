package org.rhasspy.mobile.platformspecific.porcupine

import io.ktor.utils.io.core.Closeable
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineError
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption

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