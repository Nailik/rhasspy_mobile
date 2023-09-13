package org.rhasspy.mobile.platformspecific.ktor

import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.engine.ApplicationEngineEnvironmentBuilder
import io.ktor.server.engine.BaseApplicationEngine
import io.ktor.server.engine.embeddedServer
import org.rhasspy.mobile.platformspecific.application.NativeApplication

/**
 * adds call logging to web server
 */
actual fun Application.installCompression() {
    //TODO #515
}

/**
 * enables compression for web server
 */
actual fun Application.installCallLogging() {
    //TODO #515
}

/**
 * create connector for webserver with ssl settings if enabled
 */
actual fun ApplicationEngineEnvironmentBuilder.installConnector(
    nativeApplication: NativeApplication,
    port: Int,
    isUseSSL: Boolean,
    keyStoreFile: String,
    keyStorePassword: String,
    keyAlias: String,
    keyPassword: String
) {
    //TODO #515
}

/**
 * get server engine
 */
actual fun getEngine(environment: ApplicationEngineEnvironment): BaseApplicationEngine {
    return embeddedServer(factory = CIO, environment = environment) //TODO #515
}