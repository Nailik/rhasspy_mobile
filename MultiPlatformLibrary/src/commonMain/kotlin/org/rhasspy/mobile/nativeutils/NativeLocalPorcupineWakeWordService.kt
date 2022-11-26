package org.rhasspy.mobile.nativeutils

import org.rhasspy.mobile.data.PorcupineLanguageOptions
import org.rhasspy.mobile.middleware.ErrorType
import org.rhasspy.mobile.settings.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.settings.porcupine.PorcupineDefaultKeyword

/**
 * Listens to WakeWord with Porcupine
 *
 * start listening to wake words
 * requires internet to activate porcupine the very first time
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class NativeLocalPorcupineWakeWordService(
    wakeWordPorcupineAccessToken: String,
    wakeWordPorcupineKeywordDefaultOptions: Set<PorcupineDefaultKeyword>,
    wakeWordPorcupineKeywordCustomOptions: Set<PorcupineCustomKeyword>,
    wakeWordPorcupineLanguage: PorcupineLanguageOptions,
    onKeywordDetected: (hotWord: String) -> Unit
) {

    fun start(): ErrorType.HotWordServiceError?

    /**
     * stops porcupine
     */
    fun stop()

}