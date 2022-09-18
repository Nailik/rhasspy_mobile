package org.rhasspy.mobile.nativeutils

import io.ktor.client.engine.cio.*
import io.ktor.server.application.*

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun Application.installCompression()

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun Application.installCallLogging()

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun CIOEngineConfig.configureEngine()