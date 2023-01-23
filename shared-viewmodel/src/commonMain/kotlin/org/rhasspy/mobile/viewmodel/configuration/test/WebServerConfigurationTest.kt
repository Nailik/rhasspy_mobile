package org.rhasspy.mobile.viewmodel.configuration.test

import org.koin.core.component.get
import org.rhasspy.mobile.logic.services.webserver.WebServerService

class WebServerConfigurationTest : IConfigurationTest() {

    override val serviceState get() = get<WebServerService>().serviceState

    init {
        //start web server
        get<WebServerService>()
    }

}