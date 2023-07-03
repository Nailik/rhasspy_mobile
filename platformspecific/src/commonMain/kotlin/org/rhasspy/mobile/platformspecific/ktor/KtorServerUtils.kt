package org.rhasspy.mobile.platformspecific.ktor

import io.ktor.server.application.Application
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.engine.ApplicationEngineEnvironmentBuilder
import io.ktor.server.engine.BaseApplicationEngine
import org.rhasspy.mobile.platformspecific.application.INativeApplication

/**
 * adds call logging to web server
 */
expect fun Application.installCompression()

/**
 * enables compression for web server
 */
expect fun Application.installCallLogging()

/**
 * create connector for webserver with ssl settings if enabled
 */
expect fun ApplicationEngineEnvironmentBuilder.installConnector(
    nativeApplication: INativeApplication,
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