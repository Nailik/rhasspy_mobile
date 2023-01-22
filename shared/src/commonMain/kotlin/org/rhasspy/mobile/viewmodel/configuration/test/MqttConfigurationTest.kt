package org.rhasspy.mobile.viewmodel.configuration.test

import org.koin.core.component.get
import org.rhasspy.mobile.services.mqtt.MqttService

class MqttConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<MqttService>().serviceState

    init {
        get<MqttService>()
    }

}