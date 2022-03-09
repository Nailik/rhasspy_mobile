package org.rhasspy.mobile.services.native

import ai.picovoice.porcupine.Porcupine
import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import org.rhasspy.mobile.services.Application
import org.rhasspy.mobile.services.ForegroundService
import org.rhasspy.mobile.settings.ConfigurationSettings

actual object NativeLocalWakeWordService {

    private val wakeWordCallback = PorcupineManagerCallback {
        //wakeword detected
        ForegroundService.listening.value = true
    }

    private var porcupineManager: PorcupineManager? = null

    private fun initializePorcupineManger() {
        porcupineManager = PorcupineManager.Builder()
            .setAccessKey(ConfigurationSettings.wakeWordAccessToken.value)
            .setKeyword(Porcupine.BuiltInKeyword.valueOf(ConfigurationSettings.wakeWordKeywordOption.data.name))
            .setSensitivity(ConfigurationSettings.wakeWordKeywordSensitivity.data)
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