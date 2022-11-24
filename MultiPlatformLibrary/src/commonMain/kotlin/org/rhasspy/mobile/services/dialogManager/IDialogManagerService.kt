package org.rhasspy.mobile.services.dialogManager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.data.DialogManagementOptions
import org.rhasspy.mobile.data.SpeechToTextOptions
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.hotword.HotWordService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.udp.UdpService

/**
 * idle
 * startedsession
 *
 */

abstract class IDialogManagerService : IService() {

    val params by inject<DialogManagerServiceParams>()
    val mqttService by inject<MqttService>()
    val hotWordService by inject<HotWordService>()
    val udpService by inject<UdpService>()
    val sessionId: String = "45j89"

    private var scope = CoroutineScope(Dispatchers.Default)

    override fun onClose() {
        scope.cancel()
    }

    companion object : KoinComponent {
        fun getService(): IDialogManagerService {
            val params = get<DialogManagerServiceParams>()
            return when (params.option) {
                DialogManagementOptions.Local -> DialogManagerLocalService()
                DialogManagementOptions.RemoteMQTT -> DialogManagerMqttService()
                DialogManagementOptions.Disabled -> DialogManagerDisabledService()
            }
        }
    }

    abstract fun startSessionMqtt()

    abstract fun endSessionMqtt(sessionId: String?)

    abstract fun startedSessionMqtt(sessionId: String?)

    abstract fun sessionEndedMqtt(sessionId: String?)

    abstract fun startListeningMqtt(sessionId: String?, isSendAudioCaptured: Boolean)

    abstract fun stopListeningMqtt(sessionId: String?)

    abstract fun intentTranscribedMqtt(sessionId: String?, text: String?)

    abstract fun hotWordDetectedMqtt(hotWord: String)

    //after recognizeIntent
    abstract fun intentTranscribedErrorMqtt(sessionId: String?)

    //after recognizeIntent
    abstract fun intentNotRecognizedMqtt(sessionId: String?)

    abstract fun intentRecognizedMqtt(sessionId: String?, intentName: String?, intent: String)

    //**********from webserver

    abstract fun listenForCommandWebServer()

    abstract fun startRecordingWebServer()

    abstract fun stopRecordingWebServer()

    //local
    abstract fun hotWordDetectedLocal(hotWord: String)

    abstract fun silenceDetectedLocal()

    suspend fun audioFrameLocal(byteData: List<Byte>) {
        //send to udp when enabled
        if (params.isUdpOutputEnabled) {
            scope.launch {
                udpService.streamAudio(byteData)
            }
        }
        //send to mqtt if necessary
        if (params.speechToTextOption == SpeechToTextOptions.RemoteMQTT) {
            scope.launch {
                mqttService.audioFrame(byteData)
            }
        }
    }

    suspend fun audioFrameWakeWordLocal(byteData: List<Byte>) {
        //send to mqtt when necessary
        if (params.wakeWordOption == WakeWordOption.MQTT) {
            scope.launch {
                mqttService.audioFrame(byteData)
            }
        }
    }

}

enum class DialogManagerState(){
    IDLE
}