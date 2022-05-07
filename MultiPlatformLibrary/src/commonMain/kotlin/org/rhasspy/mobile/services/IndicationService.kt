package org.rhasspy.mobile.services.indication

import co.touchlab.kermit.Logger
import com.badoo.reaktive.observable.doOnAfterNext
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.logic.State
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.nativeutils.NativeIndication
import org.rhasspy.mobile.settings.AppSettings

object IndicationService {
    private val logger = Logger.withTag("IndicationService")

    init {
        //change things according to state of the service
        StateMachine.currentState.doOnAfterNext {
            logger.v { "currentState changed to $it" }
            when (it) {
                State.StartingSession -> {
                    //TODO here hot word detected
                }
                State.StartedSession -> {
                    //TODO or here hot word detected, sound when start session from remote?
                    //hot word detected -> indication
                    //light + sound?
                }
                State.RecordingIntent -> {

                    //Indication that recording is running
                }
                State.RecordingStopped -> {
                    //indictaion that recording has stopped
                }
                State.TranscribingIntent -> {
                    //indication that it's thinking
                }
                State.TranscribingError -> {

                    //error indication
                }
                State.RecognizingIntent -> {
                    //indication that it's thinking
                }
                State.RecognizingIntentError -> {

                    //error indication
                }
                State.IntentHandling -> {
                    //working indication
                }
                else -> {
                    //no indication
                }
            }
        }
    }

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