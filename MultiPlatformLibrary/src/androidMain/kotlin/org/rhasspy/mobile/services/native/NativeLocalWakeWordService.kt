package org.rhasspy.mobile.services.native

import ai.picovoice.porcupine.Porcupine
import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import co.touchlab.kermit.Logger
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.services.ServiceInterface
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

        try {

            val keywordName =
                ConfigurationSettings.wakeWordPorcupineKeywordOptions.data.elementAt(ConfigurationSettings.wakeWordPorcupineKeywordOption.data)

            val buildInKeyword = findBuiltInKeyword(keywordName)

            val porcupineBuilder = PorcupineManager.Builder()
                .setAccessKey(ConfigurationSettings.wakeWordPorcupineAccessToken.data)
                .setSensitivity(ConfigurationSettings.wakeWordPorcupineKeywordSensitivity.data).apply {
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
        }
    }

    /**
     * invoked when a WakeWord is detected, informs listening service
     */
    override fun invoke(keywordIndex: Int) {
        logger.d { "invoke - keyword detected" }

        ServiceInterface.hotWordDetected()
    }

    private fun findBuiltInKeyword(keywordName: String): Porcupine.BuiltInKeyword? {
        return try {
            Porcupine.BuiltInKeyword.valueOf(keywordName)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}