package org.rhasspy.mobile.viewmodel.configuration.test

import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingService

class IntentHandlingConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<IntentHandlingService>().serviceState
    fun handleIntent(intentName: String, intent: String) {
        testScope.launch {
            get<IntentHandlingService>().intentHandling(intentName, intent)
        }
    }

}