package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import org.koin.core.component.get
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationTest

class DialogManagementConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<DialogManagerService>().serviceState

    init {
        get<MqttService>()
    }

}