package org.rhasspy.mobile.services.indication

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.nativeutils.NativeIndication
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.localaudio.LocalAudioService
import org.rhasspy.mobile.settings.AppSetting

class IndicationService : IService(), KoinComponent {
    private val logger = LogType.IndicationService.logger()

    private val localAudioService by inject<LocalAudioService>()

    //states are used by overlay
    private val _isShowVisualIndication = MutableStateFlow(false)
    val isShowVisualIndication = _isShowVisualIndication.readOnly
    private val _indicationState = MutableStateFlow(IndicationState.Idle)
    val indicationState = _indicationState.readOnly

    /**
     * idle shows no indication and stops screen wakeup
     */
    fun onIdle() {
        logger.d { "onIdle" }
        _isShowVisualIndication.value = false
        NativeIndication.releaseWakeUp()
    }

    /**
     * wake up screen when hotword is detected and play sound eventually
     */
    fun onWakeWordDetected() {
        logger.d { "onWakeWordDetected" }
        if (AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.value) {
            NativeIndication.wakeUpScreen()
        }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
        _indicationState.value = IndicationState.WakeUp
        if (AppSetting.isSoundIndicationEnabled.value) {
            localAudioService.playWakeSound()
        }
    }

    /**
     * update indication state
     */
    fun onListening() {
        logger.d { "onListening" }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
        _indicationState.value = IndicationState.Recording
    }

    /**
     * play sound that speech was recorded
     */
    fun onSilenceDetected() {
        logger.d { "onSilenceDetected" }
        if (AppSetting.isSoundIndicationEnabled.value) {
            localAudioService.playRecordedSound()
        }
    }

    /**
     * when intent is recognized show thinking animation
     */
    fun onRecognizingIntent() {
        logger.d { "onRecognizingIntent" }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
        _indicationState.value = IndicationState.Thinking
    }

    /**
     * show animation that audio is playing
     */
    fun onPlayAudio() {
        logger.d { "onPlayAudio" }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
        _indicationState.value = IndicationState.Speaking
    }

    /**
     * play error sound on error
     */
    fun onError() {
        logger.d { "onError" }
        if (AppSetting.isSoundIndicationEnabled.value) {
            localAudioService.playErrorSound()
        }
    }

}
