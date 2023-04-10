package org.rhasspy.mobile.viewmodel.configuration.mqtt

import org.koin.core.component.get
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationTest

class MqttConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<MqttService>().serviceState

    init {
        get<MqttService>()
    }

}