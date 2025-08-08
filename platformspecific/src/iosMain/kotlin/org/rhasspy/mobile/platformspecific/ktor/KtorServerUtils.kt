package org.rhasspy.mobile.platformspecific.ktor

import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.EmbeddedServer
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
actual fun ApplicationEngine.Configuration.installConnector(
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
actual fun getEngine(
    configure: ApplicationEngine.Configuration.() -> Unit,
    module: Application.() -> Unit
): EmbeddedServer<*, *> {
    return embeddedServer(
        factory = CIO,
        configure = configure,
        module = module,
    ) //TODO("Not yet implemented")
}