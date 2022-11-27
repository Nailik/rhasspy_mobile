package org.rhasspy.mobile.viewModels.configuration.test

import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.services.webserver.WebServerService

class WebServerConfigurationTest : IConfigurationTest() {

    init {
        println("init WebServerConfigurationTest $this")
    }
    fun runTest() {
        testScope.launch {
            //start web server
            get<WebServerService>()
        }
    }

    override fun onClose() {
        //nothing to do
    }

}