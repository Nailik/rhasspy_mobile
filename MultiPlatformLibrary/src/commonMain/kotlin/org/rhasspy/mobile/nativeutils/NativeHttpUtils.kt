package org.rhasspy.mobile.nativeutils

import io.ktor.client.engine.cio.*
import io.ktor.server.application.*
import io.ktor.server.engine.*

/**
 * adds call logging to web server
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun Application.installCallLogging()

/**
 * enables compression for web server
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun Application.installCompression()

/**
 * create connector for webserver with ssl settings if enabled
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun ApplicationEngineEnvironmentBuilder.installConnector(
    port: Int,
    isUseSSL: Boolean,
    keyStoreFile: String,
    keyStorePassword: String,
    keyAlias: String,
    keyPassword: String
)

/**
 * get server engine
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun getEngine(environment: ApplicationEngineEnvironment): BaseApplicationEngine

/**
 * configure client engine
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun CIOEngineConfig.configureEngine(isHttpVerificationDisabled: Boolean)