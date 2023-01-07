package org.rhasspy.mobile.nativeutils

import ai.picovoice.porcupine.*
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import co.touchlab.kermit.Logger
import io.ktor.utils.io.core.*
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.services.wakeword.PorcupineError
import org.rhasspy.mobile.services.wakeword.PorcupineErrorType
import org.rhasspy.mobile.settings.option.PorcupineLanguageOption
import org.rhasspy.mobile.settings.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.settings.porcupine.PorcupineDefaultKeyword
import java.io.File

/**
 * Listens to WakeWord with Porcupine
 *
 * checks for audio permission
 */
actual class PorcupineWakeWordClient actual constructor(
    private val wakeWordPorcupineAccessToken: String,
    private val wakeWordPorcupineKeywordDefaultOptions: Set<PorcupineDefaultKeyword>,
    private val wakeWordPorcupineKeywordCustomOptions: Set<PorcupineCustomKeyword>,
    private val wakeWordPorcupineLanguage: PorcupineLanguageOption,
    private val onKeywordDetected: (hotWord: String) -> Unit,
    private val onError: (PorcupineError) -> Unit
) : PorcupineManagerCallback, Closeable {
    private val logger = Logger.withTag("NativeLocalWakeWordService")

    //manager to stop start and reload porcupine
    private var porcupineManager: PorcupineManager? = null

    /**
     * create porcupine client
     */
    actual fun initialize(): PorcupineError? {
        if (ActivityCompat.checkSelfPermission(
                Application.nativeInstance,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            logger.e { "missing recording permission" }
            return PorcupineError(null, PorcupineErrorType.MicrophonePermissionMissing)
        }

        return try {
            val porcupineBuilder = PorcupineManager.Builder()
                .setAccessKey(wakeWordPorcupineAccessToken)
                //keyword paths can not be used with keywords, therefore also the built in keywords are copied to a usable file location
                .setKeywordPaths(
                    wakeWordPorcupineKeywordDefaultOptions.filter { it.isEnabled && it.option.language == wakeWordPorcupineLanguage }
                        .map {
                            copyBuildInKeywordFileIfNecessary(it)
                        }.toMutableList().also { list ->
                            list.addAll(
                                wakeWordPorcupineKeywordCustomOptions.filter { it.isEnabled }.map {
                                    File(
                                        Application.nativeInstance.filesDir,
                                        "porcupine/${it.fileName}"
                                    ).absolutePath
                                }
                            )
                        }.toTypedArray()
                )
                .setSensitivities(
                    wakeWordPorcupineKeywordDefaultOptions.filter { it.isEnabled && it.option.language == wakeWordPorcupineLanguage }
                        .map {
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
                .setErrorCallback {
                    onError(it.toPorcupineError())
                }


            File(Application.nativeInstance.filesDir, "sounds").mkdirs()

            porcupineManager = porcupineBuilder.build(Application.nativeInstance, this)

            null//no error
        } catch (e: Exception) {
            return e.toPorcupineError()
        }
    }


    /**
     * start wake word detected
     *
     * start listening to wake words
     * requires internet to activate porcupine the very first time
     *
     * checks for audio permission
     * tries to start porcupine
     */
    actual fun start(): PorcupineError? {
        porcupineManager?.let {
            it.start()
            return null
        } ?: run {
            logger.a { "Porcupine start but porcupineManager not initialized" }
            return PorcupineError(null, PorcupineErrorType.NotInitialized)
        }
    }

    /**
     * stop wake word detected
     */
    actual fun stop() {
        porcupineManager?.stop()
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

    /**
     * copies a model file from the file resources to app storage directory, else cannot be used by porcupine
     */
    private fun copyModelFileIfNecessary(): String {
        val file = File(Application.nativeInstance.filesDir, "porcupine/model_${wakeWordPorcupineLanguage.name.lowercase()}.pv")
        file.mkdirs()

        if (!file.exists()) {
            file.outputStream().write(
                Application.nativeInstance.resources.openRawResource(wakeWordPorcupineLanguage.file.rawResId)
                    .readBytes()
            )
        }

        return file.absolutePath
    }

    /**
     * copies a keyword file from the file resources to app storage directory, else cannot be used by porcupine
     */
    private fun copyBuildInKeywordFileIfNecessary(defaultKeyword: PorcupineDefaultKeyword): String {
        val file = File(
            Application.nativeInstance.filesDir,
            "porcupine/model_${defaultKeyword.option.name.lowercase()}.ppn"
        )

        if (!file.exists()) {
            file.outputStream().write(Application.nativeInstance.resources.openRawResource(defaultKeyword.option.file.rawResId).readBytes())
        }

        return file.absolutePath
    }

    /**
     * deletes the porcupine manager
     */
    override fun close() {
        porcupineManager?.delete()
        porcupineManager = null
    }

    /**
     * converts an exception to a porcupine depending on type
     */
    private fun Exception.toPorcupineError(): PorcupineError {
        return when (this) {
            is PorcupineActivationException -> PorcupineError(this, PorcupineErrorType.ActivationException)
            is PorcupineActivationLimitException -> PorcupineError(this, PorcupineErrorType.ActivationLimitException)
            is PorcupineActivationRefusedException -> PorcupineError(this, PorcupineErrorType.ActivationRefusedException)
            is PorcupineActivationThrottledException -> PorcupineError(this, PorcupineErrorType.ActivationThrottledException)
            is PorcupineInvalidArgumentException -> PorcupineError(this, PorcupineErrorType.InvalidArgumentException)
            is PorcupineInvalidStateException -> PorcupineError(this, PorcupineErrorType.InvalidStateException)
            is PorcupineIOException -> PorcupineError(this, PorcupineErrorType.IOException)
            is PorcupineKeyException -> PorcupineError(this, PorcupineErrorType.KeyException)
            is PorcupineMemoryException -> PorcupineError(this, PorcupineErrorType.MemoryException)
            is PorcupineRuntimeException -> PorcupineError(this, PorcupineErrorType.RuntimeException)
            is PorcupineStopIterationException -> PorcupineError(this, PorcupineErrorType.StopIterationException)
            else -> PorcupineError(this, PorcupineErrorType.Other)
        }
    }
}