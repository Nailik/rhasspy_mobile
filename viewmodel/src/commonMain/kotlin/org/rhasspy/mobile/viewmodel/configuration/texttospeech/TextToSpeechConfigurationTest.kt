package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationTest

class TextToSpeechConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<TextToSpeechService>().serviceState
    fun startTextToSpeech(text: String) {
        testScope.launch {
            //await for mqtt
            get<MqttService>()
                .isHasStarted
                .map { it }
                .distinctUntilChanged()
                .first { it }

            get<TextToSpeechService>().textToSpeech("", text)
        }
    }

}