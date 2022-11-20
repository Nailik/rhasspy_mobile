package org.rhasspy.mobile.services.indication

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.nativeutils.NativeIndication
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.ServiceWatchdog
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.sounds.SoundOptions

object IndicationService : IService() {
    private val logger = Logger.withTag("IndicationService")

    private val _indicationState = MutableStateFlow(IndicationState.Idle)
    private val _showVisualIndication = MutableStateFlow(false)
    val showVisualIndication = _showVisualIndication.readOnly
    val indicationState = _indicationState.readOnly

    private val serviceWatchdog by inject<ServiceWatchdog>()

    init {
        if (AppSettings.isWakeWordLightIndicationEnabled.value) {
            if (!NativeIndication.checkPermission()) {
                serviceWatchdog.indicationServiceOverlayPermissionMissing()
            }
        }
    }

    override fun onClose() {
        //nothing to do
    }

    /**
     * called when session ended
     */
    fun onIdle() {
        _showVisualIndication.value = false
        NativeIndication.releaseWakeUp()
    }

    fun onWakeUp() {
        if (AppSettings.isWakeWordDetectionTurnOnDisplayEnabled.value) {
            NativeIndication.wakeUpScreen()
        }
        if (AppSettings.isWakeWordLightIndicationEnabled.value) {
            _showVisualIndication.value = true
        }
        _indicationState.value = IndicationState.WakeUp
        playWakeSound()
    }

    fun onRecording() {
        if (AppSettings.isWakeWordLightIndicationEnabled.value) {
            _showVisualIndication.value = true
        }
        _indicationState.value = IndicationState.Recording
    }

    fun onRecordingFinished() {
        playRecordedSound()
    }

    fun onThinking() {
        if (AppSettings.isWakeWordLightIndicationEnabled.value) {
            _showVisualIndication.value = true
        }
        _indicationState.value = IndicationState.Thinking
    }

    fun onSpeaking() {
        if (AppSettings.isWakeWordLightIndicationEnabled.value) {
            _showVisualIndication.value = true
        }
        _indicationState.value = IndicationState.Speaking
    }

    fun onErrorIntent() {
        playErrorSound()
    }

    private fun playWakeSound() {
        if (AppSettings.isSoundIndicationEnabled.value) {
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
    }

    private fun playRecordedSound() {
        if (AppSettings.isSoundIndicationEnabled.value) {
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
    }

    private fun playErrorSound() {
        if (AppSettings.isSoundIndicationEnabled.value) {
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

}
