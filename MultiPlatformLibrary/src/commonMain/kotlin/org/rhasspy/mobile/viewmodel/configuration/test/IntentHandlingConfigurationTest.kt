package org.rhasspy.mobile.viewmodel.configuration.test

import org.koin.core.component.get
import org.rhasspy.mobile.services.intenthandling.IntentHandlingService

class IntentHandlingConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<IntentHandlingService>().serviceState
    fun startTest() {

    }

    override fun onClose() {
        //TODO("Not yet implemented")
    }
}