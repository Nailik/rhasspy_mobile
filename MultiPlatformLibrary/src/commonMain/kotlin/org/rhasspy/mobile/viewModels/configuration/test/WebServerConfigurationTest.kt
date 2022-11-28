package org.rhasspy.mobile.viewModels.configuration.test

import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.services.webserver.WebServerService

class WebServerConfigurationTest : IConfigurationTest() {

    init {
        println("init WebServerConfigurationTest $this")

        testScope.launch {
            //start web server
            get<WebServerService>()
        }
    }

    fun runTest() {
        //nothing to do
    }

    override fun onClose() {
        //nothing to do
    }

}