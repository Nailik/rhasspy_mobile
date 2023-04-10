package org.rhasspy.mobile.viewmodel.configuration.webserver

import org.koin.core.component.get
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationTest

class WebServerConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<WebServerService>().serviceState

    init {
        //start web server
        get<WebServerService>()
    }

}