package org.rhasspy.mobile.services.webserver

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.services.IServiceTest
import org.rhasspy.mobile.services.webserver.data.WebServerServiceStateType

class WebServerServiceTest(
    val service: WebServerLink
) : IServiceTest("WebServerService") {

    init {
        CoroutineScope(Dispatchers.Default).launch {
            service.receivedRequest.collect {
                success(WebServerServiceStateType.RECEIVING, it.second.path)
            }
        }
        service.start()
    }

}