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
    //TODO("Not yet implemented")
}

/**
 * enables compression for web server
 */
actual fun Application.installCallLogging() {
    //TODO("Not yet implemented")
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
    //TODO("Not yet implemented")
}

/**
 * get server engine
 */
actual fun getEngine(environment: ApplicationEngineEnvironment): BaseApplicationEngine {
    return embeddedServer(factory = CIO, environment = environment) //TODO("Not yet implemented")
}