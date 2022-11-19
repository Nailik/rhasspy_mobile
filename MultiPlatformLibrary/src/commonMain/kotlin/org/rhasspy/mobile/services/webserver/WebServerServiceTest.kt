package org.rhasspy.mobile.services.webserver

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.services.IServiceLink
import org.rhasspy.mobile.services.IServiceTest
import org.rhasspy.mobile.services.webserver.data.WebServerServiceStateType

class WebServerServiceTest(
    private val webServerLink: WebServerLink
) : IServiceTest("WebServerService", webServerLink), KoinComponent {

    override fun onStartTest(scope: CoroutineScope) {
        scope.launch {
            webServerLink.receivedRequest.collect {
                success(WebServerServiceStateType.RECEIVING, it.second.path)
            }
        }
    }

    override fun getService(): WebServerService = get()

}