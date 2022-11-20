package org.rhasspy.mobile.services.hotword

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.logic.State
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.nativeutils.NativeLocalPorcupineWakeWordService
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.settings.AppSettings

/**
 * hot word services listens if hotword is enabled and the current state of the state machine
 *
 * according to this it starts and stops the native hot word service or eventually starts recording to send the speech data to mqtt
 */
class HotWordService : IService() {
    private val logger = Logger.withTag("HotWordService")
    private var isRunning = false

    private val params by inject<HotWordServiceParams>()
    private var nativeLocalPorcupineWakeWordService: NativeLocalPorcupineWakeWordService? = null

    /**
     * starts the service
     * observes state machine and hotWordEnabled setting
     * to start and stop recording for wake word
     */
    init {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            AppSettings.isHotWordEnabled.data.collect {
                logger.v { "isHotWordEnabled changed to $it" }
                evaluateHotWordAction(StateMachine.currentState.value, it)
            }
        }

        scope.launch {
            //change things according to state of the service
            StateMachine.currentState.collect {
                logger.v { "currentState changed to $it" }
                evaluateHotWordAction(it, AppSettings.isHotWordEnabled.value)
            }
        }
    }

    override fun onClose() {
        TODO("Not yet implemented")
    }

    private suspend fun evaluateHotWordAction(state: State, enabled: Boolean) {
        logger.v { "evaluateHotWordAction state $state enabled $enabled running $isRunning" }
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
    private suspend fun start() {
        isRunning = true
        logger.d { "startHotWord" }

        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {
                //when porcupine is used for hotWord then start local service
                if (params.wakeWordPorcupineAccessToken.isNotEmpty()) {
                    nativeLocalPorcupineWakeWordService = NativeLocalPorcupineWakeWordService(
                        params.wakeWordPorcupineKeywordDefaultOptions,
                        params.wakeWordPorcupineKeywordCustomOptions,
                        params.wakeWordPorcupineLanguage
                    )
                } else {
                    val description = "couldn't start local wake word service, access Token Empty"
                    StateMachine.hotWordError(description)
                    logger.e { description }
                }
            }
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
            WakeWordOption.MQTT -> TODO() //RecordingService.startRecordingWakeWord()
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
        nativeLocalPorcupineWakeWordService?.stop()
        //stop recorder for wake word, will determine internally if recording is stopped completely or resumed for intent recoording
    TODO() //    RecordingService.stopRecordingWakeWord()
    }

}