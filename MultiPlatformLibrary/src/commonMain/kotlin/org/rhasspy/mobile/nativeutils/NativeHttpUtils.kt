package org.rhasspy.mobile.nativeutils

import io.ktor.client.engine.cio.*
import io.ktor.server.engine.*
import io.ktor.server.application.*
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun Application.installCompression()

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun Application.installCallLogging()

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun ApplicationEngineEnvironmentBuilder.installConnector(
    port: Int,
    isUseSSL: Boolean,
    keyStoreFile: String,
    keyStorePassword: String,
    keyAlias: String,
    keyPassword: String
)


@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun getEngine(environment: ApplicationEngineEnvironment): BaseApplicationEngine

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun CIOEngineConfig.configureEngine(isHttpVerificationDisabled: Boolean)