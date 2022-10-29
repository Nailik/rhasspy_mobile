package org.rhasspy.mobile.nativeutils

import ai.picovoice.porcupine.Porcupine
import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import co.touchlab.kermit.Logger
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.PorcupineLanguageOptions
import org.rhasspy.mobile.settings.ConfigurationSettings
import java.io.File

/**
 * Listens to WakeWord with Porcupine
 */
actual object NativeLocalWakeWordService : PorcupineManagerCallback {
    private val logger = Logger.withTag("NativeLocalWakeWordService")

    //manager to stop start and reload porcupine
    private var porcupineManager: PorcupineManager? = null

    /**
     * start listening to wake words
     * requires internet to activate porcupine the very first time
     */
    actual fun start() {
        logger.d { "start" }

        initializePorcupineManger()
        porcupineManager?.start()
    }

    /**
     * stops porcupine
     */
    actual fun stop() {
        logger.d { "stop" }

        porcupineManager?.stop()
    }

    /**
     * initialize porcupine with access token, internet access necessary
     */
    private fun initializePorcupineManger() {
        logger.d { "initializePorcupineManger" }


        if (ActivityCompat.checkSelfPermission(Application.Instance, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            logger.e { "missing recording permission" }
            return
        }

        /*     try {

                 val keywordName =
                     ConfigurationSettings.wakeWordPorcupineKeywordOptions.value.elementAt(ConfigurationSettings.wakeWordPorcupineKeywordOption.value)

                 val buildInKeyword = findBuiltInKeyword(keywordName)

                 val porcupineBuilder = PorcupineManager.Builder()
                     .setAccessKey(ConfigurationSettings.wakeWordPorcupineAccessToken.value)
                     .setSensitivity(ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.value).apply {
                         setModelPath(copyModelFileIfNecessary())
                         buildInKeyword?.also {
                             setKeyword(it)
                         } ?: run {
                             setKeywordPath(File(Application.Instance.filesDir, "porcupine/$keywordName").absolutePath)
                         }
                     }
                 File(Application.Instance.filesDir, "sounds").mkdirs()

                 porcupineManager = porcupineBuilder.build(Application.Instance, this)

             } catch (e: Exception) {
                 logger.e(e) { "initializePorcupineManger failed" }
             }*/
    }

    /**
     * invoked when a WakeWord is detected, informs listening service
     */
    override fun invoke(keywordIndex: Int) {
        logger.d { "invoke - keyword detected" }
        //    val keywordName =
        //         ConfigurationSettings.wakeWordPorcupineKeywordOptions.value.elementAt(ConfigurationSettings.wakeWordPorcupineKeywordOption.value)
        //      StateMachine.hotWordDetected(keywordName)
    }

    private fun findBuiltInKeyword(keywordName: String): Porcupine.BuiltInKeyword? {
        return try {
            Porcupine.BuiltInKeyword.valueOf(keywordName)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private fun copyModelFileIfNecessary(): String {
        val file =
            File(Application.Instance.filesDir, "porcupine/model_${ConfigurationSettings.wakeWordPorcupineLanguage.data.value.name.lowercase()}.pv")

        if (!file.exists()) {
            val modelFile = when (ConfigurationSettings.wakeWordPorcupineLanguage.data.value) {
                PorcupineLanguageOptions.EN -> MR.files.porcupine_params
                PorcupineLanguageOptions.DE -> MR.files.porcupine_params_de
                PorcupineLanguageOptions.FR -> MR.files.porcupine_params_fr
                PorcupineLanguageOptions.ES -> MR.files.porcupine_params_es
            }

            file.outputStream().write(Application.Instance.resources.openRawResource(modelFile.rawResId).readBytes())
        }

        return file.absolutePath
    }
}