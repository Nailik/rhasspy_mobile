package org.rhasspy.mobile.platformspecific.ktor

import android.annotation.SuppressLint
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.websocket.WebSocketDeflateExtension
import io.ktor.websocket.WebSocketExtensionsConfig
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.net.Inet4Address
import java.net.InetAddress
import java.security.cert.X509Certificate
import java.util.Collections
import java.util.concurrent.TimeUnit
import java.util.zip.Deflater
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

actual fun WebSocketExtensionsConfig.installDeflate() {
    install(WebSocketDeflateExtension) {
        /**
         * Compression level to use for [java.util.zip.Deflater].
         */
        compressionLevel = Deflater.DEFAULT_COMPRESSION

        /**
         * Prevent compressing small outgoing frames.
         */
        compressIfBiggerThan(bytes = 4 * 1024)
    }
}

class DnsSelector() : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        return Dns.SYSTEM.lookup(hostname).filterIsInstance<Inet4Address>()
    }
}

actual fun HttpClientF(
    block: HttpClientConfig<*>.() -> Unit
): HttpClient {

    return HttpClient(
        engineFactory = OkHttp,
        block = {
            engine {
                config {
                    followRedirects(true)
                    followSslRedirects(true)

                    retryOnConnectionFailure(true)
                }
                //without dns selector websocket doesn't work ???
                preconfigured = OkHttpClient.Builder()
                    .dns(DnsSelector())
                    .build()
            }
            block()
        }
    )
}