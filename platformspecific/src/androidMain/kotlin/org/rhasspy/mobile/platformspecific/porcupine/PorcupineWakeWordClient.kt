package org.rhasspy.mobile.platformspecific.porcupine

import ai.picovoice.porcupine.PorcupineException
import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import co.touchlab.kermit.Logger
import kotlinx.collections.immutable.ImmutableList
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import java.io.File

/**
 * Listens to WakeWord with Porcupine
 *
 * checks for audio permission
 */
actual class PorcupineWakeWordClient actual constructor(
    private val wakeWordPorcupineAccessToken: String,
    private val wakeWordPorcupineKeywordDefaultOptions: ImmutableList<PorcupineDefaultKeyword>,
    private val wakeWordPorcupineKeywordCustomOptions: ImmutableList<PorcupineCustomKeyword>,
    private val wakeWordPorcupineLanguage: PorcupineLanguageOption,
    private val onKeywordDetected: (hotWord: String) -> Unit,
    private val onError: (Exception) -> Unit
) : PorcupineManagerCallback, KoinComponent {
    private val logger = Logger.withTag("PorcupineWakeWordClient")

    //manager to stop start and reload porcupine
    private var porcupineManager: PorcupineManager? = null

    private val context = (get<INativeApplication>() as NativeApplication)

    private var initialized: Boolean = false
    actual val isInitialized: Boolean = initialized

    /**
     * create porcupine client
     */
    actual fun initialize(): Exception? {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            logger.e { "missing recording permission" }
            return Exception("MicrophonePermissionMissing")
        }

        return try {
            val porcupineBuilder = PorcupineManager.Builder()
                .setAccessKey(wakeWordPorcupineAccessToken)
                //keyword paths can not be used with keywords, therefore also the built in keywords are copied to a usable file location
                .setKeywordPaths(
                    wakeWordPorcupineKeywordDefaultOptions.filter { it.isEnabled && it.option.language == wakeWordPorcupineLanguage }
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
                .setModelPath(copyModelFile())
                .setErrorCallback {
                    onError(it)
                }


            File(context.filesDir, "sounds").mkdirs()

            porcupineManager = porcupineBuilder.build(context, this)

            initialized = true
            null//no error
        } catch (exception: PorcupineException) {
            initialized = false
            return Exception(exception.localizedMessage)
        } catch (exception: Exception) {
            initialized = false
            return exception
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
    actual fun start(): Exception? {
        return porcupineManager?.let {
            return try {
                it.start()
                null
            } catch (exception: Exception) {
                exception
            }
        } ?: run {
            logger.a { "Porcupine start but porcupineManager not initialized" }
            Exception("notInitialized")
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
    private fun copyModelFile(): String {
        val folder = File(context.filesDir, "porcupine")
        folder.mkdirs()
        val file = File(folder, "model_${wakeWordPorcupineLanguage.name.lowercase()}.pv")

        file.outputStream().write(
            context.resources.openRawResource(wakeWordPorcupineLanguage.file.rawResId)
                .readBytes()
        )

        return file.absolutePath
    }

    /**
     * copies a keyword file from the file resources to app storage directory, else cannot be used by porcupine
     */
    private fun copyBuildInKeywordFile(defaultKeyword: PorcupineDefaultKeyword): String {
        val folder = File(context.filesDir, "porcupine")
        folder.mkdirs()
        val file = File(folder, "keyword_${defaultKeyword.option.name.lowercase()}.ppn")

        file.outputStream().write(
            context.resources.openRawResource(defaultKeyword.option.file.rawResId)
                .readBytes()
        )

        return file.absolutePath
    }

    /**
     * deletes the porcupine manager
     */
    actual fun close() {
        porcupineManager?.stop()
        porcupineManager?.delete()
        porcupineManager = null
    }

}