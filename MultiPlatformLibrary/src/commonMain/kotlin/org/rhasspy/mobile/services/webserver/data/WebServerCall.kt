package org.rhasspy.mobile.services.webserver.data

import io.ktor.server.application.*

data class WebServerCall(
    val call: ApplicationCall,
    val path: WebServerPath
)