package org.rhasspy.mobile.services.hotword

import co.touchlab.kermit.Logger
import org.koin.core.component.inject
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.middleware.EventType.HotWordServiceEventType.Detected
import org.rhasspy.mobile.middleware.EventType.HotWordServiceEventType.StartPorcupine
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.middleware.action.LocalAction
import org.rhasspy.mobile.nativeutils.NativeLocalPorcupineWakeWordService
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.recording.RecordingService
import org.rhasspy.mobile.services.udp.UdpService

/**
 * hot word services listens for hot word, evaluates configuration settings but no states
 *
 * calls stateMachineService when hot word was detected
 */
class HotWordService : IService() {
    private val logger = Logger.withTag("HotWordService")

    private val params by inject<HotWordServiceParams>()
    private val udpService by inject<UdpService>()
    private val mqttService by inject<MqttService>()
    private var nativeLocalPorcupineWakeWordService: NativeLocalPorcupineWakeWordService? = null

    private val recordingService by inject<RecordingService>()

    private val serviceMiddleware by inject<IServiceMiddleware>()

    /**
     * starts the service
     */
    init {
        logger.d { "startHotWord" }
        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {
                val startPorcupineEvent = serviceMiddleware.createEvent(StartPorcupine)
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
                    startPorcupineEvent.error(it)
                } ?: run {
                    startPorcupineEvent.success()
                }
            }
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
            WakeWordOption.MQTT -> {} //nothing to do
            WakeWordOption.Udp -> {} //nothing to do
            WakeWordOption.Disabled -> logger.v { "hotWordDisabled" }
        }
    }

    fun startDetection() {
        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> nativeLocalPorcupineWakeWordService?.start()
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
          //  WakeWordOption.MQTT -> recordingService.startRecordingWakeWord()
         //   WakeWordOption.Udp -> recordingService.startRecordingWakeWord()
            WakeWordOption.Disabled -> logger.v { "hotWordDisabled" }
            else -> {}
        }
    }

    suspend fun hotWordAudioFrame(byteData: List<Byte>) {
        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> nativeLocalPorcupineWakeWordService?.start()
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
            WakeWordOption.MQTT -> mqttService.audioFrame(byteData)
            WakeWordOption.Udp -> udpService.streamAudio(byteData)
            WakeWordOption.Disabled -> logger.v { "hotWordDisabled" }
        }
    }

    fun stopDetection() {
   //     recordingService.stopRecordingWakeWord()
        nativeLocalPorcupineWakeWordService?.stop()
    }

    private fun onKeywordDetected(hotWord: String) {
        serviceMiddleware.createEvent(Detected, hotWord).success()
        serviceMiddleware.localAction(LocalAction.HotWordDetected(hotWord))
    }

    override fun onClose() {
    //    recordingService.stopRecording()
     //   recordingService.stopRecordingWakeWord()
        nativeLocalPorcupineWakeWordService?.stop()
    }

}