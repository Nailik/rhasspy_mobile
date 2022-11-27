package org.rhasspy.mobile.viewModels.configuration.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.services.webserver.WebServerService

class WebServerConfigurationTest : IConfigurationTest() {

    public override fun startTest() {
        super.startTest()
    }
    override fun onTest(scope: CoroutineScope) {
        //TODO("Not yet implemented")
    }

    override fun runTest(scope: CoroutineScope) {
        scope.launch {
            //start web server
            get<WebServerService>()
        }
    }

    override fun onClose() {
        //TODO("Not yet implemented")
    }

}