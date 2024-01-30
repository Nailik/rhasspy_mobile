package org.rhasspy.mobile.platformspecific.ktor

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.*
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
actual fun ApplicationEngine.Configuration.installConnector(
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
actual fun buildServer(module: Application.() -> Unit, configure: ApplicationEngine.Configuration.() -> Unit): ApplicationEngine {
    return embeddedServer(factory = CIO, configure = configure, module = module).engine
}