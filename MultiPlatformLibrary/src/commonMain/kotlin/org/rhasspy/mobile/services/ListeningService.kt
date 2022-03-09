package org.rhasspy.mobile.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.services.native.NativeIndication
import org.rhasspy.mobile.settings.AppSettings
import kotlin.time.Duration.Companion.seconds


object ListeningService : (Boolean) -> Unit {

    fun start() {
        ForegroundService.listening.addObserver(this)
    }

    fun stop() {
        ForegroundService.listening.removeObserver(this)
    }

    override fun invoke(listening: Boolean) {
        if (listening) {
            CoroutineScope(Dispatchers.Main).launch {

                if (AppSettings.isWakeWordSoundIndication.data) {
                    NativeIndication.playAudio(MR.files.etc_wav_beep_hi)
                }

                if (AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.data) {
                    NativeIndication.wakeUpScreen()
                }

                if (AppSettings.isWakeWordLightIndication.data) {
                    NativeIndication.showDisplayOverOtherApps()
                }

                //reset for now no automatically silence detection
                delay(20.seconds)
                ForegroundService.listening.value = false

                if (AppSettings.isWakeWordLightIndication.data) {
                    NativeIndication.closeDisplayOverOtherApps()
                }
            }
        }
    }

}