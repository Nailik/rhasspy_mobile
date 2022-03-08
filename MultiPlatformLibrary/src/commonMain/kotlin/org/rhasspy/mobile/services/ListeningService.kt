package org.rhasspy.mobile.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.audio.Audio
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
            test()
        }
    }

    private fun test() {
        CoroutineScope(Dispatchers.Main).launch {
            if (AppSettings.isWakeWordSoundIndication.data) {
                Audio.play(MR.files.etc_wav_beep_hi)
            }
            delay(2.seconds)
            ForegroundService.listening.value = false
        }
    }

}