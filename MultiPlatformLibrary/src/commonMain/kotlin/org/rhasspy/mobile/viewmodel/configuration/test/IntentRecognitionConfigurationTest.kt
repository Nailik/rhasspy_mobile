package org.rhasspy.mobile.viewmodel.configuration.test

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsServiceParams
import org.rhasspy.mobile.settings.option.IntentRecognitionOption

class IntentRecognitionConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<RhasspyActionsService>().serviceState
    fun runTest(text: String) {
        testScope.launch {
            //await for mqtt
            if (get<RhasspyActionsServiceParams>().intentRecognitionOption == IntentRecognitionOption.RemoteMQTT) {
                get<MqttService>()
                    .isHasStarted
                    .map { it }
                    .distinctUntilChanged()
                    .first { it }
            }

            val middleware = get<IServiceMiddleware>()
            //TODO     get<RhasspyActionsService>().recognizeIntent(middleware.sessionId, text)
        }
    }

    override fun onClose() {
        //TODO("Not yet implemented")
    }
}