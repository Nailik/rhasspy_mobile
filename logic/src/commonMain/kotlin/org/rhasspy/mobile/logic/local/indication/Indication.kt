package org.rhasspy.mobile.logic.local.indication

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.indication.IndicationState
import org.rhasspy.mobile.data.indication.IndicationState.*
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.platformspecific.indication.NativeIndication
import org.rhasspy.mobile.settings.AppSetting

internal interface IIndication : IDomain {

    val isShowVisualIndication: StateFlow<Boolean>
    val indicationState: StateFlow<IndicationState>

    fun onIdle()
    fun onSessionStarted()
    fun onRecording()
    fun onThinking()
    fun onPlayAudio()

}

internal class Indication : IIndication {

    private val logger = Logger.withTag("Indication")

    //states are used by overlay
    override val isShowVisualIndication = MutableStateFlow(false)
    override val indicationState = MutableStateFlow(Idle)

    /**
     * idle shows no indication and stops screen wakeup
     */
    override fun onIdle() {
        logger.d { "onIdle" }
        indicationState.value = Idle
        isShowVisualIndication.value = false
        NativeIndication.releaseWakeUp()
    }

    /**
     * wake up screen when hotword is detected and play sound eventually
     */
    override fun onSessionStarted() {
        indicationState.value = WakeUp
        logger.d { "onWakeWordDetected" }
        if (AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.value) {
            NativeIndication.wakeUpScreen()
        }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            isShowVisualIndication.value = true
        }
    }

    /**
     * update indication state
     */
    override fun onRecording() {
        indicationState.value = Recording
        logger.d { "onRecording" }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            isShowVisualIndication.value = true
        }
    }

    /**
     * when intent is recognized show thinking animation
     */
    override fun onThinking() {
        indicationState.value = Thinking
        logger.d { "onRecognizingIntent" }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            isShowVisualIndication.value = true
        }
    }

    /**
     * show animation that audio is playing
     */
    override fun onPlayAudio() {
        indicationState.value = Speaking
        logger.d { "onPlayAudio" }
        if (AppSetting.isWakeWordLightIndicationEnabled.value) {
            isShowVisualIndication.value = true
        }
    }

    override fun dispose() {}

}
