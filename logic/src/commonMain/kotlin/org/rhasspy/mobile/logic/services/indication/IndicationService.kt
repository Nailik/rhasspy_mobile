package org.rhasspy.mobile.logic.services.indication

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.data.indication.IndicationState
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.platformspecific.indication.NativeIndication
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting

class IndicationService : IService(LogType.IndicationService) {

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
    fun onWakeWordDetected(onFinished: () -> Unit) {
        logger.d { "onWakeWordDetected" }
        if (AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.value) {
            NativeIndication.wakeUpScreen()
        }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
        _indicationState.value = IndicationState.WakeUp
        if (AppSetting.isSoundIndicationEnabled.value) {
            localAudioService.playWakeSound { onFinished() }
        } else {
            onFinished()
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
    fun onThinking() {
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
