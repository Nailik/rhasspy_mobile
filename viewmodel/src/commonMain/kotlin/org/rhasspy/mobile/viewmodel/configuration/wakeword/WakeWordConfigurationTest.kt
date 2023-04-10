package org.rhasspy.mobile.viewmodel.configuration.wakeword

import org.koin.core.component.get
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationTest

class WakeWordConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<WakeWordService>().serviceState
    fun startWakeWordDetection() {
        get<WakeWordService>().startDetection()
    }

}