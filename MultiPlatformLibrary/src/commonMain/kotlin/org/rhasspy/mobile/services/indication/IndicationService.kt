package org.rhasspy.mobile.services.indication

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.nativeutils.NativeIndication
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.localaudio.LocalAudioService
import org.rhasspy.mobile.settings.AppSettings

class IndicationService : IService(), KoinComponent {

    private val localAudioService by inject<LocalAudioService>()
    private val _indicationState = MutableStateFlow(IndicationState.Idle)
    private val _isShowVisualIndication = MutableStateFlow(false)

    //states are used by overlay
    val isShowVisualIndication = _isShowVisualIndication.readOnly
    val indicationState = _indicationState.readOnly

    override fun onClose() {
    }

    /**
     * idle shows no indication and stops screen wakeup
     */
    fun onIdle() {
        _isShowVisualIndication.value = false
        NativeIndication.releaseWakeUp()
    }

    /**
     * wake up screen when hotword is detected and play sound eventually
     */
    fun onWakeWordDetected() {
        if (AppSettings.isWakeWordDetectionTurnOnDisplayEnabled.value) {
            NativeIndication.wakeUpScreen()
        }
        if (AppSettings.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
        _indicationState.value = IndicationState.WakeUp
        if (AppSettings.isSoundIndicationEnabled.value) {
            localAudioService.playWakeSound()
        }
    }

    /**
     * update indication state
     */
    fun onListening() {
        if (AppSettings.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
        _indicationState.value = IndicationState.Recording
    }

    /**
     * play sound that speech was recorded
     */
    fun onSilenceDetected() {
        if (AppSettings.isSoundIndicationEnabled.value) {
            localAudioService.playRecordedSound()
        }
    }

    /**
     * when intent is recognized show thinking animation
     */
    fun onRecognizingIntent() {
        if (AppSettings.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
        _indicationState.value = IndicationState.Thinking
    }

    /**
     * show animation that audio is playing
     */
    fun onPlayAudio() {
        if (AppSettings.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
        _indicationState.value = IndicationState.Speaking
    }

    /**
     * play error sound on error
     */
    fun onError() {
        if (AppSettings.isSoundIndicationEnabled.value) {
            localAudioService.playErrorSound()
        }
    }

}
