package org.rhasspy.mobile.platformspecific.ktor

import io.ktor.server.application.Application
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import org.rhasspy.mobile.platformspecific.application.NativeApplication

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
expect fun ApplicationEngine.Configuration.installConnector(
    nativeApplication: NativeApplication,
    port: Int,
    isUseSSL: Boolean,
    keyStoreFile: String,
    keyStorePassword: String,
    keyAlias: String,
    keyPassword: String,
)

/**
 * get server engine
 */
expect fun getEngine(
    configure: ApplicationEngine.Configuration.() -> Unit,
    module: Application.() -> Unit,
): EmbeddedServer<*, *>