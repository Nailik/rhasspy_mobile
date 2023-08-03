package org.rhasspy.mobile.platformspecific.ktor

import android.annotation.SuppressLint
import io.ktor.client.engine.cio.*
import io.ktor.network.tls.extensions.*
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

/**
 * configure client engine
 */
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