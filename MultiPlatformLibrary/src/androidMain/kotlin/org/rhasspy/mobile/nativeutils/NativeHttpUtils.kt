package org.rhasspy.mobile.nativeutils

import android.annotation.SuppressLint
import io.ktor.client.engine.cio.*
import io.ktor.network.tls.certificates.*
import io.ktor.network.tls.extensions.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import java.io.File
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager
import kotlin.text.toCharArray

actual fun Application.installCompression() {
    install(Compression) {
        gzip()
    }
}

actual fun Application.installCallLogging() {
    install(CallLogging) {
        level = org.slf4j.event.Level.INFO
    }
}

actual fun ApplicationEngineEnvironmentBuilder.installConnector(
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
                org.rhasspy.mobile.Application.Instance.filesDir,
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

actual fun getEngine(environment: ApplicationEngineEnvironment): BaseApplicationEngine {
    return embeddedServer(factory = Netty, environment = environment)
}

actual fun CIOEngineConfig.configureEngine(isHttpVerificationDisabled: Boolean) {
    https {
        if (isHttpVerificationDisabled) {
            trustManager = @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate>? = null
            }
        }
    }
}