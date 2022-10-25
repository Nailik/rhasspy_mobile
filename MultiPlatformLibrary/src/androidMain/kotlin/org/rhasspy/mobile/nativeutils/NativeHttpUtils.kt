package org.rhasspy.mobile.nativeutils

import android.annotation.SuppressLint
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.gzip
import org.rhasspy.mobile.settings.ConfigurationSettings
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
        if (!ConfigurationSettings.isHttpSSLVerificationDisabled.value) {
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