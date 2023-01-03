package org.rhasspy.mobile.viewmodel.configuration.test

import org.koin.core.component.get
import org.rhasspy.mobile.services.audioplaying.AudioPlayingService

class AudioPlayingConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<AudioPlayingService>().serviceState

    fun startTest() {

    }

    override fun onClose() {
        //TODO("Not yet implemented")
    }
}