package org.rhasspy.mobile.viewModels.configuration.test

import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService

class TextToSpeechConfigurationTest : IConfigurationTest() {

    //TODO filter events
    init {
        get<MqttService>()
    }

    fun startTest(text: String) {
        testScope.launch {
            get<RhasspyActionsService>().textToSpeech("", text)
        }
    }

    override fun onClose() {
        //TODO("Not yet implemented")
    }
}