package org.rhasspy.mobile.nativeutils

import android.annotation.SuppressLint
import io.ktor.client.engine.cio.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

actual fun Application.installCompression() {
    install(Compression) {
        gzip()
    }
}

actual fun Application.installCallLogging() {
    install(CallLogging)
}

actual fun CIOEngineConfig.configureEngine() {
    https {
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