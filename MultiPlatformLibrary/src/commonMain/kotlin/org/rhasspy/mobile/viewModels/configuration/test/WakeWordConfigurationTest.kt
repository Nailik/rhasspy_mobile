package org.rhasspy.mobile.viewModels.configuration.test

import org.koin.core.component.get
import org.rhasspy.mobile.services.hotword.HotWordService

class WakeWordConfigurationTest : IConfigurationTest() {

    fun runTest() {
        get<HotWordService>().startDetection()
    }

    override fun onClose() {
        //TODO does not release microphone??
        get<HotWordService>().stopDetection()
    }

    override fun close() {
        super.close()
    }
}