package org.rhasspy.mobile.platformspecific.porcupine

import kotlinx.collections.immutable.ImmutableList
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import java.io.File

abstract class IPorcupineClient : KoinComponent {

    abstract val wakeWordPorcupineAccessToken: String
    abstract val wakeWordPorcupineKeywordDefaultOptions: ImmutableList<PorcupineDefaultKeyword>
    abstract val wakeWordPorcupineKeywordCustomOptions: ImmutableList<PorcupineCustomKeyword>
    abstract val wakeWordPorcupineLanguage: PorcupineLanguageOption
    abstract val onKeywordDetected: (keywordIndex: Int) -> Unit
    abstract val onError: (Exception) -> Unit

    internal val context = get<NativeApplication>()

    abstract fun start()
    abstract fun stop()
    abstract fun close()

    internal fun getKeywordPaths(): Array<String> {
        return wakeWordPorcupineKeywordDefaultOptions.filter { it.isEnabled && it.option.language == wakeWordPorcupineLanguage }
            .map {
                copyBuildInKeywordFile(it)
            }.toMutableList().also { list ->
                list.addAll(
                    wakeWordPorcupineKeywordCustomOptions.filter { it.isEnabled }.map {
                        File(
                            context.filesDir,
                            "porcupine/${it.fileName}"
                        ).absolutePath
                    }
                )
            }.toTypedArray()
    }

    internal fun getSensitivities(): FloatArray {
        return wakeWordPorcupineKeywordDefaultOptions.filter { it.isEnabled && it.option.language == wakeWordPorcupineLanguage }
            .map {
                it.sensitivity
            }.toMutableList().also { list ->
                list.addAll(
                    wakeWordPorcupineKeywordCustomOptions.filter { it.isEnabled }.map {
                        it.sensitivity
                    }
                )
            }.toTypedArray().toFloatArray()
    }

    /**
     * copies a model file from the file resources to app storage directory, else cannot be used by porcupine
     */
    internal fun copyModelFile(): String {
        val folder = File(context.filesDir, "porcupine")
        folder.mkdirs()
        val file = File(folder, "model_${wakeWordPorcupineLanguage.name.lowercase()}.pv")

        file.outputStream().apply {
            val inputStream = context.resources.openRawResource(wakeWordPorcupineLanguage.file.rawResId)
            write(inputStream.readBytes())
            flush()
            close()
            inputStream.close()
        }

        return file.absolutePath
    }


    /**
     * copies a keyword file from the file resources to app storage directory, else cannot be used by porcupine
     */
    private fun copyBuildInKeywordFile(defaultKeyword: PorcupineDefaultKeyword): String {
        val folder = File(context.filesDir, "porcupine")
        folder.mkdirs()
        val file = File(folder, "keyword_${defaultKeyword.option.name.lowercase()}.ppn")

        file.outputStream().apply {
            val inputStream = context.resources.openRawResource(defaultKeyword.option.file.rawResId)
            write(inputStream.readBytes())
            flush()
            close()
            inputStream.close()
        }

        return file.absolutePath
    }

}