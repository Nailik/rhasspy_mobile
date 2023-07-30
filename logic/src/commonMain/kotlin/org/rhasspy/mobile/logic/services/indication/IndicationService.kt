package org.rhasspy.mobile.logic.services.indication

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.data.indication.IndicationState
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.localaudio.ILocalAudioService
import org.rhasspy.mobile.platformspecific.indication.NativeIndication
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting

interface IIndicationService : IService {

    val isShowVisualIndication: StateFlow<Boolean>
    val indicationState: StateFlow<IndicationState>

    fun onIdle()
    fun onSessionStarted()
    fun onRecording()
    fun onSilenceDetected()
    fun onThinking()
    fun onPlayAudio()
    fun onError()

}

internal class IndicationService : IIndicationService {

    override val logger = LogType.IndicationService.logger()
    private val localAudioService by inject<ILocalAudioService>()

    //states are used by overlay
    private val _isShowVisualIndication = MutableStateFlow(false)
    override val isShowVisualIndication = _isShowVisualIndication.readOnly
    private val _indicationState = MutableStateFlow(IndicationState.Idle)
    override val indicationState = _indicationState.readOnly

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success)
    override val serviceState = _serviceState.readOnly

    /**
     * idle shows no indication and stops screen wakeup
     */
    override fun onIdle() {
        logger.d { "onIdle" }
        _isShowVisualIndication.value = false
        NativeIndication.releaseWakeUp()
    }

    /**
     * wake up screen when hotword is detected and play sound eventually
     */
    override fun onSessionStarted() {
        logger.d { "onWakeWordDetected" }
        if (AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.value) {
            NativeIndication.wakeUpScreen()
        }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
        _indicationState.value = IndicationState.WakeUp
        if (AppSetting.isSoundIndicationEnabled.value) {
            localAudioService.playWakeSound { }
        }
    }

    /**
     * update indication state
     */
    override fun onRecording() {
        logger.d { "onRecording" }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
        _indicationState.value = IndicationState.Recording
    }

    /**
     * play sound that speech was recorded
     */
    override fun onSilenceDetected() {
        logger.d { "onSilenceDetected" }
        if (AppSetting.isSoundIndicationEnabled.value) {
            localAudioService.playRecordedSound()
        }
    }

    /**
     * when intent is recognized show thinking animation
     */
    override fun onThinking() {
        logger.d { "onRecognizingIntent" }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
        _indicationState.value = IndicationState.Thinking
    }

    /**
     * show animation that audio is playing
     */
    override fun onPlayAudio() {
        logger.d { "onPlayAudio" }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
        _indicationState.value = IndicationState.Speaking
    }

    /**
     * play error sound on error
     */
    override fun onError() {
        logger.d { "onError" }
        if (AppSetting.isSoundIndicationEnabled.value) {
            localAudioService.playErrorSound()
        }
    }

}
