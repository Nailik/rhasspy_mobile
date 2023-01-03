package org.rhasspy.mobile.viewmodel.configuration.test

import org.koin.core.component.get
import org.rhasspy.mobile.services.dialog.DialogManagerService

class DialogManagementConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<DialogManagerService>().serviceState
    fun startTest() {

    }

    override fun onClose() {
        //TODO("Not yet implemented")
    }
}