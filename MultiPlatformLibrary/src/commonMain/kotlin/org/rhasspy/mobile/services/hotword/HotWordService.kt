package org.rhasspy.mobile.services.hotword

import co.touchlab.kermit.Logger
import org.koin.core.component.inject
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.nativeutils.NativeLocalPorcupineWakeWordService
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.ServiceWatchdog
import org.rhasspy.mobile.services.recording.RecordingService
import org.rhasspy.mobile.services.statemachine.StateMachineService

/**
 * hot word services listens for hot word, evaluates configuration settings but no states
 *
 * calls stateMachineService when hot word was detected
 */
class HotWordService : IService() {
    private val logger = Logger.withTag("HotWordService")

    private val params by inject<HotWordServiceParams>()
    private var nativeLocalPorcupineWakeWordService: NativeLocalPorcupineWakeWordService? = null

    private val stateMachineService by inject<StateMachineService>()
    private val serviceWatchdog by inject<ServiceWatchdog>()
    private val recordingService by inject<RecordingService>()

    /**
     * starts the service
     */
    init {
        logger.d { "startHotWord" }
        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {
                //when porcupine is used for hotWord then start local service
                nativeLocalPorcupineWakeWordService = NativeLocalPorcupineWakeWordService(
                    params.wakeWordPorcupineAccessToken,
                    params.wakeWordPorcupineKeywordDefaultOptions,
                    params.wakeWordPorcupineKeywordCustomOptions,
                    params.wakeWordPorcupineLanguage,
                    ::onKeywordDetected
                )
                val error = nativeLocalPorcupineWakeWordService?.start()
                error?.also {
                    serviceWatchdog.hotWordServiceError(error)
                }
            }
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
            WakeWordOption.MQTT -> {} //nothing to do
            WakeWordOption.Disabled -> logger.v { "hotWordDisabled" }
        }
    }

    fun startDetection() {
        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> nativeLocalPorcupineWakeWordService?.start()
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
            WakeWordOption.MQTT -> recordingService.startRecordingWakeWord()
            WakeWordOption.Disabled -> logger.v { "hotWordDisabled" }
        }
    }

    fun stopDetection() {
        recordingService.stopRecordingWakeWord()
        nativeLocalPorcupineWakeWordService?.stop()
    }

    private fun onKeywordDetected(index: Int) = stateMachineService.hotWordDetected(index)

    override fun onClose() {
        recordingService.stopRecording()
        nativeLocalPorcupineWakeWordService?.stop()
    }

}