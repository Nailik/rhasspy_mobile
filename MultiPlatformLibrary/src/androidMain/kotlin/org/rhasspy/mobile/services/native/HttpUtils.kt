package org.rhasspy.mobile.services.native

import android.annotation.SuppressLint
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

actual object HttpUtils {

    actual fun getSocketBuilder(): SocketBuilder {
        return aSocket(ActorSelectorManager(Dispatchers.IO))
    }

    actual fun getHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
        return HttpClient(CIO) {
            engine {
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
        }
    }

}