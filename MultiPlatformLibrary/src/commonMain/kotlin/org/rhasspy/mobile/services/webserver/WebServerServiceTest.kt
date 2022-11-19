package org.rhasspy.mobile.services.webserver

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.services.IServiceTest
import org.rhasspy.mobile.services.webserver.data.WebServerLinkStateType

class WebServerServiceTest(
    private val webServerLink: WebServerLink
) : IServiceTest<WebServerLinkStateType>("WebServerService", webServerLink), KoinComponent {

    override fun onStartTest(scope: CoroutineScope) {

        pending(WebServerLinkStateType.STARTING)

        scope.launch {
            webServerLink.isServerRunning.collect {
                if (it) {
                    success(WebServerLinkStateType.STARTING)
                }
            }
        }

        scope.launch {
            webServerLink.currentError.filterNotNull().collect {
                error(it.data, it.e.cause?.message ?: it.e.message)
            }
        }

        scope.launch {
            webServerLink.receivedRequest.collect {
                success(WebServerLinkStateType.RECEIVING, it.path.path)
            }
        }
    }

    override fun getService(): WebServerService = get()

}