package org.rhasspy.mobile.logic.local.indication

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.data.indication.IndicationState
import org.rhasspy.mobile.data.indication.IndicationState.*
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioPlayer
import org.rhasspy.mobile.platformspecific.indication.NativeIndication
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting

interface IIndication : IDomain {

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

internal class Indication : IIndication {

    private val logger = Logger.withTag("IndicationService")
    private val localAudioService by inject<ILocalAudioPlayer>()

    //states are used by overlay
    private val _isShowVisualIndication = MutableStateFlow(false)
    override val isShowVisualIndication = _isShowVisualIndication.readOnly
    private val _indicationState = MutableStateFlow(Idle)
    override val indicationState = _indicationState.readOnly

    /**
     * idle shows no indication and stops screen wakeup
     */
    override fun onIdle() {
        logger.d { "onIdle" }
        _indicationState.value = Idle
        _isShowVisualIndication.value = false
        NativeIndication.releaseWakeUp()
    }

    /**
     * wake up screen when hotword is detected and play sound eventually
     */
    override fun onSessionStarted() {
        _indicationState.value = WakeUp
        logger.d { "onWakeWordDetected" }
        if (AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.value) {
            NativeIndication.wakeUpScreen()
        }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
        if (AppSetting.isSoundIndicationEnabled.value) {
            localAudioService.playWakeSound()
        }
    }

    /**
     * update indication state
     */
    override fun onRecording() {
        _indicationState.value = Recording
        logger.d { "onRecording" }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
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
        _indicationState.value = Thinking
        logger.d { "onRecognizingIntent" }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
    }

    /**
     * show animation that audio is playing
     */
    override fun onPlayAudio() {
        _indicationState.value = Speaking
        logger.d { "onPlayAudio" }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            _isShowVisualIndication.value = true
        }
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

    override fun dispose() {}

}
