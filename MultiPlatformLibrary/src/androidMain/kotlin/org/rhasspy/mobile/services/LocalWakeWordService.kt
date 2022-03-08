package org.rhasspy.mobile.services

import ai.picovoice.porcupine.Porcupine
import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import org.rhasspy.mobile.settings.ConfigurationSettings

actual object LocalWakeWordService {

    private val wakeWordCallback = PorcupineManagerCallback {
        //wakeword detected
        ForegroundService.listening.value = true
    }

    private var porcupineManager: PorcupineManager? = null

    private fun initializePorcupineManger() {
        porcupineManager = PorcupineManager.Builder()
            .setAccessKey(ConfigurationSettings.wakeWordAccessToken.value)
            .setKeywords(arrayOf(Porcupine.BuiltInKeyword.valueOf("JARVIS")))
            .build(Application.Instance, wakeWordCallback)
    }

    actual fun start() {
        initializePorcupineManger()
        porcupineManager?.start()
    }

    actual fun stop() {

        porcupineManager?.stop()
    }

}