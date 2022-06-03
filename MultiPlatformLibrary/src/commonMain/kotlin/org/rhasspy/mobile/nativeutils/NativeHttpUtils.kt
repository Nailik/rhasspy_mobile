package org.rhasspy.mobile.nativeutils

import io.ktor.client.engine.cio.*
import io.ktor.server.application.*

expect fun Application.installCompression()

expect fun Application.installCallLogging()

expect fun CIOEngineConfig.configureEngine()