package org.rhasspy.mobile.viewModels.configuration.test

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.hotword.HotWordService

class WakeWordConfigurationTest : IConfigurationTest() {

    fun runTest() {
        //used for wake word recording
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