package org.rhasspy.mobile.nativeutils

import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.server.application.Application
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.engine.ApplicationEngineEnvironmentBuilder
import io.ktor.server.engine.BaseApplicationEngine

/**
 * adds call logging to web server
 */
actual fun Application.installCompression() {
    TODO("Not yet implemented")
}

/**
 * enables compression for web server
 */
actual fun Application.installCallLogging() {
    TODO("Not yet implemented")
}

/**
 * create connector for webserver with ssl settings if enabled
 */
actual fun ApplicationEngineEnvironmentBuilder.installConnector(
    port: Int,
    isUseSSL: Boolean,
    keyStoreFile: String,
    keyStorePassword: String,
    keyAlias: String,
    keyPassword: String
) {
    TODO("Not yet implemented")
}

/**
 * get server engine
 */
actual fun getEngine(environment: ApplicationEngineEnvironment): BaseApplicationEngine {
    TODO("Not yet implemented")
}

/**
 * configure client engine
 */
actual fun CIOEngineConfig.configureEngine(isHttpVerificationDisabled: Boolean) {
    TODO("Not yet implemented")
}