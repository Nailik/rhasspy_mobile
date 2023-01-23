package org.rhasspy.mobile.logic.nativeutils

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import java.io.File
import java.security.KeyStore
import kotlin.text.toCharArray

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

        keystore.load(
            File(
                nativeApplication.filesDir,
                keyStoreFile
            ).inputStream(), keyStorePassword.toCharArray()
        )

        sslConnector(
            keyStore = keystore,
            keyAlias = keyAlias,
            keyStorePassword = { keyStorePassword.toCharArray() },
            privateKeyPassword = { keyPassword.toCharArray() }) {
            // this.host = "0.0.0.0"
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