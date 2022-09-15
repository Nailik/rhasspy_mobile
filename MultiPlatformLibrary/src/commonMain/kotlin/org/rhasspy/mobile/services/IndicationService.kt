package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.logic.State
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.nativeutils.AudioPlayer
import org.rhasspy.mobile.nativeutils.NativeIndication
import org.rhasspy.mobile.observer.MutableObservable
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.sounds.SoundOptions

object IndicationService {
    private val logger = Logger.withTag("IndicationService")

    private val currentState = MutableObservable(IndicationState.Idle)
    private val showVisualIndication = MutableObservable(false)
    val showVisualIndicationUi = showVisualIndication.readOnly()
    val readonlyState = currentState.readOnly()

    init {
        //change things according to state of the service
        StateMachine.currentState.observe {
            logger.v { "currentState changed to $it" }

            //evaluate new state
            val newState = when (it) {
                //hot word detected
                State.StartedSession -> IndicationState.Wakeup
                //Indication that recording is running
                State.RecordingIntent -> IndicationState.Recording
                //indication that it's thinking
                State.TranscribingIntent -> IndicationState.Thinking
                //indication that it's thinking
                State.RecognizingIntent -> IndicationState.Thinking
                //no indication
                State.PlayingAudio -> IndicationState.Speaking
                else -> IndicationState.Idle
            }

            //set new state
            currentState.value = newState

            //handle indication (screen wakeup and light indication)
            when (newState) {
                IndicationState.Idle -> {
                    showVisualIndication.value = false
                    NativeIndication.releaseWakeUp()
                }
                IndicationState.Wakeup,
                IndicationState.Recording,
                IndicationState.Thinking,
                IndicationState.Speaking -> {
                    if (AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.value) {
                        NativeIndication.wakeUpScreen()
                    }

                    if (AppSettings.isWakeWordLightIndication.value) {
                        showVisualIndication.value = true
                    }
                }
            }

            //handle sound indication
            if (AppSettings.isWakeWordSoundIndication.value) {
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
        when (AppSettings.wakeSound.value) {
            SoundOptions.Disabled.name -> {}
            SoundOptions.Default.name -> AudioPlayer.playSoundFileResource(MR.files.etc_wav_beep_hi)
            else -> AudioPlayer.playSoundFile(AppSettings.wakeSound.value)
        }
    }

    private fun playRecordedSound() {
        when (AppSettings.recordedSound.value) {
            SoundOptions.Disabled.name -> {}
            SoundOptions.Default.name -> AudioPlayer.playSoundFileResource(MR.files.etc_wav_beep_lo)
            else -> AudioPlayer.playSoundFile(AppSettings.recordedSound.value)
        }
    }

    private fun playErrorSound() {
        when (AppSettings.errorSound.value) {
            SoundOptions.Disabled.name -> {}
            SoundOptions.Default.name -> AudioPlayer.playSoundFileResource(MR.files.etc_wav_beep_error)
            else -> AudioPlayer.playSoundFile(AppSettings.errorSound.value)
        }
    }


}
