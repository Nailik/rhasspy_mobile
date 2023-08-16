package org.rhasspy.mobile.platformspecific.porcupine

import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption

actual class PorcupineWakeWordClient actual constructor(
    wakeWordPorcupineAccessToken: String,
    wakeWordPorcupineKeywordDefaultOptions: ImmutableList<PorcupineDefaultKeyword>,
    wakeWordPorcupineKeywordCustomOptions: ImmutableList<PorcupineCustomKeyword>,
    wakeWordPorcupineLanguage: PorcupineLanguageOption,
    onKeywordDetected: (hotWord: String) -> Unit,
) {

    /**
     * start wake word detected
     */
    actual fun start(): Exception? {
        //TODO("Not yet implemented")
        return null
    }

    actual fun audioFrame(data: ByteArray) {
        //TODO("Not yet implemented")
    }

    actual fun close() {
        //TODO("Not yet implemented")
    }


}