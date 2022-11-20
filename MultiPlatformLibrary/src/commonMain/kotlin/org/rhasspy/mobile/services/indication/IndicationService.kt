package org.rhasspy.mobile.services.indication

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.logic.State
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.nativeutils.NativeIndication
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.sounds.SoundOptions

object IndicationService : IService() {
    private val logger = Logger.withTag("IndicationService")

    private val currentState = MutableStateFlow(IndicationState.Idle)
    private val showVisualIndication = MutableStateFlow(false)
    val showVisualIndicationUi: StateFlow<Boolean> get() = showVisualIndication
    val readonlyState: StateFlow<IndicationState> get() = currentState

    init {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            //change things according to state of the service
            StateMachine.currentState.collect {
                logger.v { "currentState changed to $it" }
                evaluateIndication(it, StateMachine.isPlayingAudio.value)
            }
        }

        scope.launch {
            StateMachine.isPlayingAudio.collect {
                logger.v { "isPlayingState changed to $it" }
                evaluateIndication(StateMachine.currentState.value, it)
            }
        }
    }

    override fun onClose() {
        TODO("Not yet implemented")
    }

    private fun evaluateIndication(state: State, isPlayingAudio: Boolean) {
        //evaluate new state
        val newState = when (state) {
            //hot word detected
            State.StartedSession -> IndicationState.Wakeup
            //Indication that recording is running
            State.RecordingIntent -> IndicationState.Recording
            //indication that it's thinking
            State.TranscribingIntent,
            State.RecognizingIntent -> IndicationState.Thinking
            //intent handling might be playing audio
            State.IntentHandling -> if (isPlayingAudio) {
                IndicationState.Speaking
            } else {
                IndicationState.Thinking
            }
            //no indication
            else -> {
                if (isPlayingAudio) {
                    IndicationState.Speaking
                } else {
                    IndicationState.Idle
                }
            }
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
                if (AppSettings.isWakeWordDetectionTurnOnDisplayEnabled.value) {
                    NativeIndication.wakeUpScreen()
                }

                if (AppSettings.isWakeWordLightIndicationEnabled.value) {
                    showVisualIndication.value = true
                }
            }
        }

        //handle sound indication
        if (AppSettings.isSoundIndicationEnabled.value) {
            when (state) {
                State.StartingSession -> playWakeSound()
                State.RecordingStopped -> playRecordedSound()
                State.TranscribingError,
                State.RecognizingIntentError -> playErrorSound()

                else -> {}
            }
        }
    }

    private fun playWakeSound() {
        when (AppSettings.wakeSound.value) {
            SoundOptions.Disabled.name -> {}
            SoundOptions.Default.name -> StateMachine.audioPlayer.playSoundFileResource(
                MR.files.etc_wav_beep_hi,
                AppSettings.wakeSoundVolume.data,
                AppSettings.soundIndicationOutputOption.value
            )
            else -> StateMachine.audioPlayer.playSoundFile(
                "wake",
                AppSettings.wakeSound.value,
                AppSettings.wakeSoundVolume.data,
                AppSettings.soundIndicationOutputOption.value
            )
        }
    }

    private fun playRecordedSound() {
        when (AppSettings.recordedSound.value) {
            SoundOptions.Disabled.name -> {}
            SoundOptions.Default.name -> StateMachine.audioPlayer.playSoundFileResource(
                MR.files.etc_wav_beep_lo,
                AppSettings.recordedSoundVolume.data,
                AppSettings.soundIndicationOutputOption.value
            )
            else -> StateMachine.audioPlayer.playSoundFile(
                "recorded",
                AppSettings.recordedSound.value,
                AppSettings.recordedSoundVolume.data,
                AppSettings.soundIndicationOutputOption.value
            )
        }
    }

    private fun playErrorSound() {
        when (AppSettings.errorSound.value) {
            SoundOptions.Disabled.name -> {}
            SoundOptions.Default.name -> StateMachine.audioPlayer.playSoundFileResource(
                MR.files.etc_wav_beep_error,
                AppSettings.errorSoundVolume.data,
                AppSettings.soundIndicationOutputOption.value
            )
            else -> StateMachine.audioPlayer.playSoundFile(
                "error",
                AppSettings.errorSound.value,
                AppSettings.errorSoundVolume.data,
                AppSettings.soundIndicationOutputOption.value
            )
        }
    }


}
