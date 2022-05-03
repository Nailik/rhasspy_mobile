package org.rhasspy.mobile.nativeutils

import io.ktor.server.application.*

expect fun Application.installCompression()

expect fun Application.installCallLogging()