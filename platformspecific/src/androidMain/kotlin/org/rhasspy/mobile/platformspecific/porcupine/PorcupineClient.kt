package org.rhasspy.mobile.platformspecific.porcupine

import ai.picovoice.porcupine.Porcupine
import co.touchlab.kermit.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PorcupineClient(
    val wakeWordPorcupineAccessToken: String,
    val wakeWordPorcupineKeywordDefaultOptions: List<PorcupineDefaultKeyword>,
    val wakeWordPorcupineKeywordCustomOptions: List<PorcupineCustomKeyword>,
    val wakeWordPorcupineLanguage: PorcupineLanguageOption,
    val onKeywordDetected: (keywordIndex: Int) -> Unit,
) : KoinComponent {
    private val logger = Logger.withTag("PorcupineClient")

    private val context = get<NativeApplication>()

    private val porcupine = Porcupine.Builder()
        .setAccessKey(wakeWordPorcupineAccessToken)
        .setKeywordPaths(getKeywordPaths())
        .setSensitivities(getSensitivities())
        .setModelPath(copyModelFile())
        .build(context)


    private var oldData = ShortArray(0)

    fun audioFrame(data: ByteArray) {
        try {
            var currentRecording = oldData + byteArrayToShortArray(data)

            //send to porcupine
            while (currentRecording.size >= 512) {
                //get a sized chunk
                val chunk = currentRecording.take(512).toShortArray()
                //cut remaining data
                currentRecording = currentRecording.takeLast(currentRecording.size - 512).toShortArray()

                val keywordIndex = porcupine.process(chunk)
                if (keywordIndex != -1) {
                    onKeywordDetected(keywordIndex)
                }
            }

            oldData = currentRecording
        } catch (e: Exception) {
            oldData = ShortArray(0)
            //restart
            logger.d("audioRecorder collection", e)
        }
    }

    fun close() {
        oldData = ShortArray(0)
        porcupine.delete()
    }

    private fun byteArrayToShortArray(byteArray: ByteArray): ShortArray {
        val shorts = ShortArray(byteArray.size / 2)
        ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts)
        return shorts
    }

    private fun getKeywordPaths(): Array<String> {
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

    private fun getSensitivities(): FloatArray {
        return wakeWordPorcupineKeywordDefaultOptions.filter { it.isEnabled && it.option.language == wakeWordPorcupineLanguage }
            .map { it.sensitivity.toFloat() }
            .toMutableList()
            .also { list ->
                list.addAll(
                    wakeWordPorcupineKeywordCustomOptions.filter { it.isEnabled }.map {
                        it.sensitivity.toFloat()
                    }
                )
            }.toTypedArray().toFloatArray()
    }

    /**
     * copies a model file from the file resources to app storage directory, else cannot be used by porcupine
     */
    private fun copyModelFile(): String {
        val folder = File(context.filesDir, "porcupine")
        folder.mkdirs()
        val file = File(folder, "model_${wakeWordPorcupineLanguage.name.lowercase()}.pv")

        file.outputStream().apply {
            val inputStream = context.resources.openRawResource(wakeWordPorcupineLanguage.file.rawResId)
            write(inputStream.readBytes())
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
            close()
            inputStream.close()
        }

        return file.absolutePath
    }

}