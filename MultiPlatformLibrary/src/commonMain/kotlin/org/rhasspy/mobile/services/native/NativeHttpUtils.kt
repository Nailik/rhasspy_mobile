package org.rhasspy.mobile.services.native

import io.ktor.server.application.*

expect fun Application.installCompression()

expect fun Application.installCallLogging()