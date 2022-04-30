package org.rhasspy.mobile.services

import org.rhasspy.mobile.MR
import org.rhasspy.mobile.services.native.NativeIndication
import org.rhasspy.mobile.settings.AppSettings

object IndicationService {

    private fun playWakeSound() {
        when (AppSettings.wakeSound.data) {
            0 -> NativeIndication.playSoundFileResource(MR.files.etc_wav_beep_hi)
            1 -> {}
            else -> NativeIndication.playSoundFile(AppSettings.wakeSounds.data.elementAt(AppSettings.wakeSound.data - 2))
        }
    }

    private fun playRecordedSound() {
        when (AppSettings.recordedSound.data) {
            0 -> NativeIndication.playSoundFileResource(MR.files.etc_wav_beep_lo)
            1 -> {}
            else -> NativeIndication.playSoundFile(AppSettings.recordedSounds.data.elementAt(AppSettings.recordedSound.data - 2))
        }
    }

    private fun playErrorSound() {
        when (AppSettings.errorSound.data) {
            0 -> NativeIndication.playSoundFileResource(MR.files.etc_wav_beep_error)
            1 -> {}
            else -> NativeIndication.playSoundFile(AppSettings.errorSounds.data.elementAt(AppSettings.errorSound.data - 2))
        }
    }



    /**
     * call the native indication and show/hide necessary indications
     */
    private fun indication(show: Boolean) {
        logger.d { "toggle indication show: $show" }

        if (show) {
            if (AppSettings.isWakeWordSoundIndication.data) {
                playWakeSound()
            }

            if (AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.data) {
                NativeIndication.wakeUpScreen()
            }

            if (AppSettings.isWakeWordLightIndication.data) {
                NativeIndication.showIndication()
            }
        } else {
            NativeIndication.closeIndicationOverOtherApps()
            NativeIndication.releaseWakeUp()
        }
    }

}