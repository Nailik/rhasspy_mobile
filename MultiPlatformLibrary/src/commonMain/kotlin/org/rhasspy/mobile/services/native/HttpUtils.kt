package org.rhasspy.mobile.services.native

import io.ktor.client.*
import io.ktor.network.sockets.*

expect object HttpUtils {

    fun getSocketBuilder(): SocketBuilder

    actual fun getHttpClient(
        block: HttpClientConfig<*>.() -> Unit
    ): HttpClient

}