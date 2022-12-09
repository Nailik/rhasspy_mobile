package org.rhasspy.mobile.viewModels.configuration.test

import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.services.webserver.WebServerService

class WebServerConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<WebServerService>().currentState
    fun runTest() {
        testScope.launch {
            //start web server
            get<WebServerService>()
        }
    }

}