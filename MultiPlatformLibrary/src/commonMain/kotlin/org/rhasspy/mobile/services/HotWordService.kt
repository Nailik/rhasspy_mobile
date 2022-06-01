package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.logic.State
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.nativeutils.NativeLocalWakeWordService
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings
import kotlin.native.concurrent.ThreadLocal

/**
 * hot word services listens if hotword is enabled and the current state of the state machine
 *
 * according to this it starts and stops the native hot word service or eventually starts recording to send the speech data to mqtt
 */

@ThreadLocal
object HotWordService {
    private val logger = Logger.withTag("HotWordService")
    private var isRunning = false

    /**
     * starts the service
     * observes state machine and hotWordEnabled setting
     * to start and stop recording for wake word
     */
    init {
        AppSettings.isHotWordEnabled.data.observe {
            logger.v { "isHotWordEnabled changed to $it" }
            evaluateHotWordAction(StateMachine.currentState.value, it)
        }

        //change things according to state of the service
        StateMachine.currentState.observe {
            logger.v { "currentState changed to $it" }
            evaluateHotWordAction(it, AppSettings.isHotWordEnabled.value)
        }
    }

    private fun evaluateHotWordAction(state: State, enabled: Boolean) {
        logger.v { "evaluateHotWorAction state $state enabled $enabled running $isRunning" }
        when (state) {
            State.AwaitingHotWord -> {
                if (enabled) {
                    if (!isRunning) {
                        //enabled and not running
                        start()
                    }
                } else if (isRunning) {
                    //not enabled but running
                    stop()
                }
            }
            else -> {
                if (isRunning) {
                    //in another state but running
                    stop()
                }
            }
        }
    }

    /**
     * start hotWord services
     */
    private fun start() {
        isRunning = true
        logger.d { "startHotWord" }

        when (ConfigurationSettings.wakeWordOption.value) {
            WakeWordOption.Porcupine -> {
                //when porcupine is used for hotWord then start local service
                if (ConfigurationSettings.wakeWordPorcupineAccessToken.value.isNotEmpty()) {
                    NativeLocalWakeWordService.start()
                } else {
                    val description = "couldn't start local wake word service, access Token Empty"
                    StateMachine.hotWordError(description)
                    logger.e { description }
                }
            }
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
            WakeWordOption.MQTT -> RecordingService.startRecordingWakeWord()
            WakeWordOption.Disabled -> logger.v { "hotWordDisabled" }
        }
    }

    /**
     * stop hotWord services
     * doesn't check for config setting because they might have changed, stop all the hotWord Services
     */
    private fun stop() {
        isRunning = false

        logger.d { "stopHotWord" }
        //make sure it is stopped
        NativeLocalWakeWordService.stop()
        //stop recorder for wake word, will determine internally if recording is stopped completely or resumed for intent recoording
        RecordingService.stopRecordingWakeWord()
    }

}