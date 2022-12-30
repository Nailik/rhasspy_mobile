package org.rhasspy.mobile.nativeutils

import io.ktor.client.engine.cio.*
import io.ktor.server.application.*
import io.ktor.server.engine.*

/**
 * adds call logging to web server
 */
expect fun Application.installCallLogging()

/**
 * enables compression for web server
 */
expect fun Application.installCompression()

/**
 * create connector for webserver with ssl settings if enabled
 */
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
expect fun getEngine(environment: ApplicationEngineEnvironment): BaseApplicationEngine

/**
 * configure client engine
 */
expect fun CIOEngineConfig.configureEngine(isHttpVerificationDisabled: Boolean)