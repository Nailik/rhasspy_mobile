package org.rhasspy.mobile.nativeutils

import ai.picovoice.porcupine.*
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import co.touchlab.kermit.Logger
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.data.PorcupineLanguageOptions
import org.rhasspy.mobile.middleware.ErrorType.HotWordServiceError
import org.rhasspy.mobile.settings.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.settings.porcupine.PorcupineDefaultKeyword
import java.io.File
import java.util.Collections.addAll

/**
 * Listens to WakeWord with Porcupine
 *
 * checks for audio permission
 */
actual class NativeLocalPorcupineWakeWordService actual constructor(
    private val wakeWordPorcupineAccessToken: String,
    private val wakeWordPorcupineKeywordDefaultOptions: Set<PorcupineDefaultKeyword>,
    private val wakeWordPorcupineKeywordCustomOptions: Set<PorcupineCustomKeyword>,
    private val wakeWordPorcupineLanguage: PorcupineLanguageOptions,
    private val onKeywordDetected: (hotWord: String) -> Unit
) : PorcupineManagerCallback {
    private val logger = Logger.withTag("NativeLocalWakeWordService")

    //manager to stop start and reload porcupine
    private var porcupineManager: PorcupineManager? = null

    /**
     * start listening to wake words
     * requires internet to activate porcupine the very first time
     *
     * checks for audio permission
     * tries to start porcupine
     */
    actual fun start(): HotWordServiceError? {
        porcupineManager?.start()

        if (ActivityCompat.checkSelfPermission(Application.Instance, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            logger.e { "missing recording permission" }
            return HotWordServiceError.MicrophonePermissionMissing
        }

        return try {
            val porcupineBuilder = PorcupineManager.Builder()
                .setAccessKey(wakeWordPorcupineAccessToken)
                //keyword paths can not be used with keywords, therefore also the built in keywords are copied to a usable file location
                .setKeywordPaths(
                    wakeWordPorcupineKeywordDefaultOptions.filter { it.isEnabled }.map {
                        copyBuildInKeywordFileIfNecessary(it)
                    }.toMutableList().also { list ->
                        list.addAll(
                            wakeWordPorcupineKeywordCustomOptions.filter { it.isEnabled }.map {
                                File(Application.Instance.filesDir, "porcupine/${it.fileName}").absolutePath
                            }
                        )
                    }.toTypedArray()
                )
                .setSensitivities(
                    wakeWordPorcupineKeywordDefaultOptions.filter { it.isEnabled }.map {
                        it.sensitivity
                    }.toMutableList().also { list ->
                        list.addAll(
                            wakeWordPorcupineKeywordCustomOptions.filter { it.isEnabled }.map {
                                it.sensitivity
                            }
                        )
                    }.toTypedArray().toFloatArray()
                )
                .setModelPath(copyModelFileIfNecessary())

            File(Application.Instance.filesDir, "sounds").mkdirs()

            porcupineManager = porcupineBuilder.build(Application.Instance, this)

            null//no error
        } catch (e: Exception) {
            return when (e) {
                is PorcupineActivationException -> HotWordServiceError.PorcupineActivationException
                is PorcupineActivationLimitException -> HotWordServiceError.PorcupineActivationLimitException
                is PorcupineActivationRefusedException -> HotWordServiceError.PorcupineActivationRefusedException
                is PorcupineActivationThrottledException -> HotWordServiceError.PorcupineActivationThrottledException
                is PorcupineInvalidArgumentException -> HotWordServiceError.PorcupineInvalidArgumentException
                is PorcupineInvalidStateException -> HotWordServiceError.PorcupineInvalidStateException
                is PorcupineIOException -> HotWordServiceError.PorcupineIOException
                is PorcupineKeyException -> HotWordServiceError.PorcupineKeyException
                is PorcupineMemoryException -> HotWordServiceError.PorcupineMemoryException
                is PorcupineRuntimeException -> HotWordServiceError.PorcupineRuntimeException
                is PorcupineStopIterationException -> HotWordServiceError.PorcupineStopIterationException
                else -> HotWordServiceError.Unknown
            }
        }
    }

    /**
     * stops porcupine
     */
    actual fun stop() {
        //TODO does not release microphone??
        logger.d { "delete" }
        porcupineManager?.delete()
    }

    /**
     * invoked when a WakeWord is detected, informs listening service
     */
    override fun invoke(keywordIndex: Int) {
        logger.d { "invoke - keyword detected" }

        val allKeywords = wakeWordPorcupineKeywordDefaultOptions.filter { it.isEnabled }.map {
            it.option.name
        }.toMutableList().apply {
            addAll(wakeWordPorcupineKeywordCustomOptions.filter { it.isEnabled }.map {
                it.fileName
            }.toMutableList())
        }

        if (allKeywords.size > keywordIndex) {
            onKeywordDetected(allKeywords[keywordIndex])
        } else {
            onKeywordDetected("Unknown")
        }
    }

    private fun copyModelFileIfNecessary(): String {
        val file = File(Application.Instance.filesDir, "porcupine/model_${wakeWordPorcupineLanguage.name.lowercase()}.pv")

        if (!file.exists()) {
            file.outputStream().write(Application.Instance.resources.openRawResource(wakeWordPorcupineLanguage.file.rawResId).readBytes())
        }

        return file.absolutePath
    }

    private fun copyBuildInKeywordFileIfNecessary(defaultKeyword: PorcupineDefaultKeyword): String {
        val file = File(Application.Instance.filesDir, "porcupine/model_${defaultKeyword.option.name.lowercase()}.ppn")

        if (!file.exists()) {
            file.outputStream().write(Application.Instance.resources.openRawResource(defaultKeyword.option.file.rawResId).readBytes())
        }

        return file.absolutePath
    }
}