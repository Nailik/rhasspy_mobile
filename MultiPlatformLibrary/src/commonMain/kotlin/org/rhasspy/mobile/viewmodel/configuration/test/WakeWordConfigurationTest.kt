package org.rhasspy.mobile.viewmodel.configuration.test

import org.koin.core.component.get
import org.rhasspy.mobile.services.wakeword.WakeWordService

class WakeWordConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<WakeWordService>().serviceState
    fun runTest() {
        get<WakeWordService>().startDetection()
    }

    override fun onClose() {
        //TODO does not release microphone??
        get<WakeWordService>().stopDetection()
    }

    override fun close() {
        super.close()
    }
}