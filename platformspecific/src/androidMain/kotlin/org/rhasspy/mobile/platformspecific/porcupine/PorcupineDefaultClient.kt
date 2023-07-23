package org.rhasspy.mobile.platformspecific.porcupine

import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption

class PorcupineDefaultClient(
    override val wakeWordPorcupineAccessToken: String,
    override val wakeWordPorcupineKeywordDefaultOptions: ImmutableList<PorcupineDefaultKeyword>,
    override val wakeWordPorcupineKeywordCustomOptions: ImmutableList<PorcupineCustomKeyword>,
    override val wakeWordPorcupineLanguage: PorcupineLanguageOption,
    override val onKeywordDetected: (keywordIndex: Int) -> Unit,
    override val onError: (Exception) -> Unit
) : IPorcupineClient(), PorcupineManagerCallback {

    private var porcupineManager = PorcupineManager.Builder()
        .setAccessKey(wakeWordPorcupineAccessToken)
        //keyword paths can not be used with keywords, therefore also the built in keywords are copied to a usable file location
        .setKeywordPaths(getKeywordPaths())
        .setSensitivities(getSensitivities())
        .setModelPath(copyModelFile())
        .setErrorCallback { onError(it) }
        .build(context, this)

    override fun start() {
        porcupineManager.start()
    }

    override fun stop() {
        porcupineManager.stop()
    }

    override fun close() {
        porcupineManager.stop()
        porcupineManager.delete()
    }

    override fun invoke(keywordIndex: Int) {
        onKeywordDetected(keywordIndex)
    }

}