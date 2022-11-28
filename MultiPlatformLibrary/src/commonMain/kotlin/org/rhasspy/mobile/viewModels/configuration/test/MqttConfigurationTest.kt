package org.rhasspy.mobile.viewModels.configuration.test

import org.koin.core.component.get
import org.rhasspy.mobile.services.mqtt.MqttService

class MqttConfigurationTest : IConfigurationTest() {

    fun startTest() {
        get<MqttService>()
    }

}