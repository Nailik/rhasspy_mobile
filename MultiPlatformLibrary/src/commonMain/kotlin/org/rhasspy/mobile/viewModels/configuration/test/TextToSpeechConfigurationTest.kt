package org.rhasspy.mobile.viewModels.configuration.test

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.services.ServiceResponse
import org.rhasspy.mobile.services.localaudio.LocalAudioService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService

class TextToSpeechConfigurationTest : IConfigurationTest() {

    fun startTest(text: String) {
        testScope.launch {
            //await for mqtt
            get<MqttService>()
                .isHasStarted
                .map { it }
                .distinctUntilChanged()
                .first { it }

            val middleware = get<IServiceMiddleware>()
            val result = get<RhasspyActionsService>().textToSpeech(middleware.sessionId, text)
            if (result is ServiceResponse.Success && result.data is ByteArray) {
                get<LocalAudioService>().playAudio(result.data.toMutableList())
            }
        }
    }

    override fun onClose() {}

}