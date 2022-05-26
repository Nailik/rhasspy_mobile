package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import com.badoo.reaktive.observable.doOnAfterNext
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.subject.publish.PublishSubject
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.logic.State
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.nativeutils.AudioPlayer
import org.rhasspy.mobile.nativeutils.NativeIndication
import org.rhasspy.mobile.settings.AppSettings

object IndicationService {
    private val logger = Logger.withTag("IndicationService")

    private val currentStateSubject = PublishSubject<IndicationState>()
    val currentState = currentStateSubject.observeOn(ioScheduler)

    init {
        //start native indication service
        currentState.doOnAfterNext {
            when (it) {
                IndicationState.Idle -> {
                    NativeIndication.closeIndicationOverOtherApps()
                    NativeIndication.releaseWakeUp()
                }
                IndicationState.Wakeup -> {
                    if (AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.data) {
                        NativeIndication.wakeUpScreen()
                    }

                    if (AppSettings.isWakeWordLightIndication.data) {
                        NativeIndication.showIndication()
                    }
                }
                else -> {}
            }
        }

        //change things according to state of the service
        StateMachine.currentState.doOnAfterNext {
            logger.v { "currentState changed to $it" }

            //evaluate new state
            val newState = when (it) {
                //hot word detected
                State.StartingSession -> IndicationState.Wakeup
                //Indication that recording is running
                State.RecordingIntent -> IndicationState.Recording
                //indication that it's thinking
                State.TranscribingIntent -> IndicationState.Thinking
                //indication that it's thinking
                State.RecognizingIntent -> IndicationState.Thinking
                //no indication
                else -> IndicationState.Idle
            }

            //set new state
            currentStateSubject.onNext(newState)

            //handle indication (screen wakeup and light indication)
            when (newState) {
                IndicationState.Idle -> {
                    NativeIndication.closeIndicationOverOtherApps()
                    NativeIndication.releaseWakeUp()
                }
                IndicationState.Wakeup -> {
                    if (AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.data) {
                        NativeIndication.wakeUpScreen()
                    }

                    if (AppSettings.isWakeWordLightIndication.data) {
                        NativeIndication.showIndication()
                    }
                }
                else -> {}
            }

            //handle sound indication
            if (AppSettings.isWakeWordSoundIndication.data) {
                when (it) {
                    State.StartingSession -> playWakeSound()
                    State.RecordingStopped -> playRecordedSound()
                    State.TranscribingError,
                    State.RecognizingIntentError -> playErrorSound()
                    else -> {}
                }
            }
        }
    }

    private fun playWakeSound() {
        when (AppSettings.wakeSound.data) {
            0 -> AudioPlayer.playSoundFileResource(MR.files.etc_wav_beep_hi)
            1 -> {}
            else -> AudioPlayer.playSoundFile(AppSettings.wakeSounds.data.elementAt(AppSettings.wakeSound.data - 2))
        }
    }

    private fun playRecordedSound() {
        when (AppSettings.recordedSound.data) {
            0 -> AudioPlayer.playSoundFileResource(MR.files.etc_wav_beep_lo)
            1 -> {}
            else -> AudioPlayer.playSoundFile(AppSettings.recordedSounds.data.elementAt(AppSettings.recordedSound.data - 2))
        }
    }

    private fun playErrorSound() {
        when (AppSettings.errorSound.data) {
            0 -> AudioPlayer.playSoundFileResource(MR.files.etc_wav_beep_error)
            1 -> {}
            else -> AudioPlayer.playSoundFile(AppSettings.errorSounds.data.elementAt(AppSettings.errorSound.data - 2))
        }
    }


}
