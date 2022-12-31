package org.rhasspy.mobile.viewmodel.configuration.test

import org.koin.core.component.get
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService

class AudioPlayingConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<RhasspyActionsService>().currentState
    public fun startTest() {

    }

    override fun onClose() {
        //TODO("Not yet implemented")
    }
}