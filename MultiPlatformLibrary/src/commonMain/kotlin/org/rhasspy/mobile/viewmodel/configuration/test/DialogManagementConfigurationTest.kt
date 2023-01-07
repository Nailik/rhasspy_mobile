package org.rhasspy.mobile.viewmodel.configuration.test

import org.koin.core.component.get
import org.rhasspy.mobile.services.dialog.DialogManagerService
import org.rhasspy.mobile.services.mqtt.MqttService

class DialogManagementConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<DialogManagerService>().serviceState

    init {
        get<MqttService>()
    }

}