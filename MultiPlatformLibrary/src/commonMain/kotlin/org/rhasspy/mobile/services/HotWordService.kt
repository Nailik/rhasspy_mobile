package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import com.badoo.reaktive.observable.doOnAfterNext
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.services.MqttService.hotWordError
import org.rhasspy.mobile.services.logic.State
import org.rhasspy.mobile.services.logic.StateMachine
import org.rhasspy.mobile.services.native.NativeLocalWakeWordService
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
     * observes state machine to start and stop recording for wake word
     */
    init {
        //change things according to state of the service
        StateMachine.currentState.doOnAfterNext {
            when (it) {
                State.AwaitingHotWord -> {
                    if (!isRunning) {
                        startHotWord()
                    }
                }
                else -> {
                    if (isRunning) {
                        stopHotWord()
                    }
                }
            }
        }
    }

    /**
     * start hotWord services
     */
    private fun startHotWord() {
        isRunning = true
        logger.d { "startHotWord" }

        when (ConfigurationSettings.wakeWordOption.data) {
            WakeWordOption.Porcupine -> {
                //when porcupine is used for hotWord then start local service
                if (ConfigurationSettings.wakeWordPorcupineAccessToken.data.isNotEmpty()) {
                    NativeLocalWakeWordService.start()
                } else {
                    val description = "couldn't start local wake word service, access Token Empty"
                    hotWordError(description)
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
    private fun stopHotWord() {
        isRunning = false

        logger.d { "stopHotWord" }
        //make sure it is stopped
        NativeLocalWakeWordService.stop()
        //stop recorder for wake word, will determine internally if recording is stopped completely or resumed for intent recoording
        RecordingService.stopRecordingWakeWord()
    }

}