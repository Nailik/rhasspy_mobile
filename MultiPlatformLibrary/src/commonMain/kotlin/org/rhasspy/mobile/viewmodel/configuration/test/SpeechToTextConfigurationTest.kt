package org.rhasspy.mobile.viewmodel.configuration.test

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.services.speechtotext.SpeechToTextServiceParams
import org.rhasspy.mobile.settings.option.SpeechToTextOption

class SpeechToTextConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<SpeechToTextService>().serviceState
    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.readOnly

    fun toggleRecording() {
        testScope.launch {
            if (get<SpeechToTextServiceParams>().speechToTextOption == SpeechToTextOption.RemoteMQTT) {
                //await for mqtt service to start if necessary
                get<MqttService>()
                    .isHasStarted
                    .map { it }
                    .distinctUntilChanged()
                    .first { it }
            }

            //await for mqtt to be started
            val speechToTextService = get<SpeechToTextService>()
            val middleware = get<IServiceMiddleware>()

            if (!isRecording.value) {
                _isRecording.value = true
                testScope.launch {
                    //TODO rhasspyActionsService.startSpeechToText(middleware.sessionId)
                }
            } else {
                _isRecording.value = false
                //execute
                testScope.launch {
                    //TODO  val response = rhasspyActionsService.endSpeechToText(middleware.sessionId, false)

                    //    if (response is ServiceResponse.Success) {
                    //      println(response.data)
                    //  }
                }
            }
        }
    }

    override fun onClose() {}

}