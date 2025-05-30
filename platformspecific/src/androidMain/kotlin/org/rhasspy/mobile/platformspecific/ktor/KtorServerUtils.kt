package org.rhasspy.mobile.platformspecific.ktor

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.*
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
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
actual fun ApplicationEngineEnvironmentBuilder.installConnector(
    nativeApplication: NativeApplication,
    port: Int,
    isUseSSL: Boolean,
    keyStoreFile: String,
    keyStorePassword: String,
    keyAlias: String,
    keyPassword: String
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
actual fun getEngine(environment: ApplicationEngineEnvironment): BaseApplicationEngine {
    return embeddedServer(factory = Netty, environment = environment)
}