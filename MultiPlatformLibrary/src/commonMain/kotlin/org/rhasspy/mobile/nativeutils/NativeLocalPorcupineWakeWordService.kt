package org.rhasspy.mobile.nativeutils

import org.rhasspy.mobile.data.PorcupineLanguageOptions
import org.rhasspy.mobile.services.hotword.HotWordServiceError
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
    onKeywordDetected: (index: Int) -> Unit
) {

    fun start(): HotWordServiceError?

    /**
     * stops porcupine
     */
    fun stop()

}