package org.rhasspy.mobile.viewModels.configuration.test

import org.koin.core.component.get
import org.rhasspy.mobile.services.dialogManager.DialogManagerService

class DialogManagementConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<DialogManagerService>().currentState
    public fun startTest() {

    }

    override fun onClose() {
        //TODO("Not yet implemented")
    }
}