package org.rhasspy.mobile.services.hotword

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.middleware.EventType.HotWordServiceEventType.*
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.middleware.action.LocalAction
import org.rhasspy.mobile.nativeutils.NativeLocalPorcupineWakeWordService
import org.rhasspy.mobile.readOnly
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

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.readOnly

    private var recording: Job? = null

    /**
     * starts the service
     */
    init {
        logger.d { "startHotWord" }
        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {
                val initializePorcupineEvent = serviceMiddleware.createEvent(InitializePorcupine)
                //when porcupine is used for hotWord then start local service
                nativeLocalPorcupineWakeWordService = NativeLocalPorcupineWakeWordService(
                    params.wakeWordPorcupineAccessToken,
                    params.wakeWordPorcupineKeywordDefaultOptions,
                    params.wakeWordPorcupineKeywordCustomOptions,
                    params.wakeWordPorcupineLanguage,
                    ::onKeywordDetected
                )
                val error = nativeLocalPorcupineWakeWordService?.initialize()
                error?.also {
                    initializePorcupineEvent.error(it)
                } ?: run {
                    initializePorcupineEvent.success()
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
            WakeWordOption.Porcupine -> {
                _isRecording.value = true
                val startPorcupineEvent = serviceMiddleware.createEvent(StartPorcupine)
                val error = nativeLocalPorcupineWakeWordService?.start()
                error?.also {
                    startPorcupineEvent.error(it)
                } ?: run {
                    startPorcupineEvent.success()
                }
            }
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
            WakeWordOption.MQTT,
            WakeWordOption.Udp -> {
                _isRecording.value = true
                //collect audio from recorder
                if (recording == null) {
                    recording = CoroutineScope(Dispatchers.Default).launch {
                        recordingService.output.collect(::hotWordAudioFrame)
                    }
                }
            }
            WakeWordOption.Disabled -> logger.v { "hotWordDisabled" }
        }
    }

    private suspend fun hotWordAudioFrame(byteData: List<Byte>) {
        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> nativeLocalPorcupineWakeWordService?.start()
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
            WakeWordOption.MQTT -> mqttService.audioFrame(byteData)
            WakeWordOption.Udp -> udpService.streamAudio(byteData)
            WakeWordOption.Disabled -> logger.v { "hotWordDisabled" }
        }
    }

    fun stopDetection() {
        _isRecording.value = false
        recording?.cancel()
        recording = null
        recordingService.close()
        nativeLocalPorcupineWakeWordService?.stop()
    }

    private fun onKeywordDetected(hotWord: String) {
        serviceMiddleware.createEvent(Detected, hotWord).success()
        serviceMiddleware.localAction(LocalAction.HotWordDetected(hotWord))
    }

    override fun onClose() {
        stopDetection()
        nativeLocalPorcupineWakeWordService?.delete()
    }

}