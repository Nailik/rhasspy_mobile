package org.rhasspy.mobile.services.native

import ai.picovoice.porcupine.Porcupine
import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import org.rhasspy.mobile.services.Application
import org.rhasspy.mobile.services.ListeningService
import org.rhasspy.mobile.settings.ConfigurationSettings

/**
 * Listens to WakeWord with Porcupine
 */
actual object NativeLocalWakeWordService : PorcupineManagerCallback {

    //manager to stop start and reload porcupine
    private var porcupineManager: PorcupineManager? = null

    /**
     * start listening to wake words
     * requires internet to activate porcupine the very first time
     */
    actual fun start() {
        initializePorcupineManger()
        porcupineManager?.start()
    }

    /**
     * stops porcupine
     */
    actual fun stop() {
        porcupineManager?.stop()
    }

    /**
     * initialize porcupine with access token, internet access necessary
     */
    private fun initializePorcupineManger() {
        porcupineManager = PorcupineManager.Builder()
            .setAccessKey(ConfigurationSettings.wakeWordAccessToken.value)
            .setKeyword(Porcupine.BuiltInKeyword.valueOf(ConfigurationSettings.wakeWordKeywordOption.data.name))
            .setSensitivity(ConfigurationSettings.wakeWordKeywordSensitivity.data)
            .build(Application.Instance, this)
    }

    /**
     * invoked when a WakeWord is detected, informs listening service
     */
    override fun invoke(keywordIndex: Int) {
        ListeningService.wakeWordDetected()
    }

}