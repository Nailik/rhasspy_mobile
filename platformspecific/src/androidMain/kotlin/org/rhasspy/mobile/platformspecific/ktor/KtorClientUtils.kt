package org.rhasspy.mobile.platformspecific.ktor

import android.annotation.SuppressLint
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.websocket.WebSocketDeflateExtension
import io.ktor.websocket.WebSocketExtensionsConfig
import okhttp3.Dns
import okhttp3.OkHttpClient
import org.rhasspy.mobile.platformspecific.ktor.SslSettings.getSslContext
import org.rhasspy.mobile.platformspecific.ktor.SslSettings.getTrustManager
import java.io.FileInputStream
import java.net.Inet4Address
import java.net.InetAddress
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.util.zip.Deflater
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object SslSettings {
    private fun getKeyStore(): KeyStore {
        val keyStoreFile = FileInputStream("keystore.jks")
        val keyStorePassword = "foobar".toCharArray()
        val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(keyStoreFile, keyStorePassword)
        return keyStore
    }

    private fun getTrustManagerFactory(): TrustManagerFactory? {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(getKeyStore())
        return trustManagerFactory
    }

    fun getSslContext(): SSLContext? {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, getTrustManagerFactory()?.trustManagers, null)
        return sslContext
    }

    fun getTrustManager(): X509TrustManager {
        @SuppressLint("CustomX509TrustManager")
        val custom = object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate>? = null
        }
        return custom
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

class DnsSelector : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        return Dns.SYSTEM.lookup(hostname).filterIsInstance<Inet4Address>()
    }
}

actual fun createClient(
    isSSLVerificationDisabled: Boolean,
    block: HttpClientConfig<*>.() -> Unit
): HttpClient {

    return HttpClient(
        engineFactory = OkHttp,
        block = {

            engine {

                config {
                    if(isSSLVerificationDisabled) {
                        sslSocketFactory(getSslContext()!!.socketFactory, getTrustManager())
                    }
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