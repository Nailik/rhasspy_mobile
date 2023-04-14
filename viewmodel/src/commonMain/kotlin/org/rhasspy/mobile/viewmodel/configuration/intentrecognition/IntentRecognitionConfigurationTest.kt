package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionServiceParams
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationTest

class IntentRecognitionConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<IntentRecognitionService>().serviceState
    fun runIntentRecognition(text: String) {
        testScope.launch {
            //await for mqtt
            if (get<IntentRecognitionServiceParams>().intentRecognitionOption == IntentRecognitionOption.RemoteMQTT) {
                get<MqttService>()
                    .isHasStarted
                    .map { it }
                    .distinctUntilChanged()
                    .first { it }
            }

            get<IntentRecognitionService>().recognizeIntent("", text)
        }
    }

}