package org.rhasspy.mobile.platformspecific.ktor

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.websocket.WebSocketExtensionsConfig

actual fun WebSocketExtensionsConfig.installDeflate() {
    //TODO #515
}

actual fun createClient(
    isSSLVerificationDisabled: Boolean,
    block: HttpClientConfig<*>.() -> Unit
): HttpClient {
    //TODO #515
    return HttpClient()
}