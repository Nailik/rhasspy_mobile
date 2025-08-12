package org.rhasspy.mobile.platformspecific.ktor

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.gzip
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import java.io.File
import java.security.KeyStore

/**
 * adds call logging to web server
 */
actual fun Application.installCompression() {
    install(Compression) {
        gzip()
    }
}

/**
 * enables compression for web server
 */
actual fun Application.installCallLogging() {
    install(CallLogging) {
        level = org.slf4j.event.Level.INFO
    }
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
    keyPassword: String,
) {
    if (isUseSSL) {
        val keystore = KeyStore.getInstance(KeyStore.getDefaultType())

        val file = File(nativeApplication.filesDir, keyStoreFile)

        keystore.load(file.inputStream(), keyStorePassword.toCharArray())

        sslConnector(
            keyStore = keystore,
            keyAlias = keyAlias,
            keyStorePassword = { keyStorePassword.toCharArray() },
            privateKeyPassword = { keyPassword.toCharArray() },
        ) {
            // this.host = "0.0.0.0"
            this.keyStorePath = file
            this.port = port
        }
    } else {
        connector {
            this.port = port
        }
    }
}

/**
 * get server engine
 */
actual fun getEngine(
    configure: ApplicationEngine.Configuration.() -> Unit,
    module: Application.() -> Unit,
): EmbeddedServer<*, *> {
    return embeddedServer(
        factory = Netty,
        configure = configure,
        module = module
    )
}