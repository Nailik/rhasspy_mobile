package org.rhasspy.mobile.platformspecific.ktor

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.websocket.WebSocketExtensionsConfig

expect fun CIOEngineConfig.configureEngine(isHttpVerificationDisabled: Boolean)

expect fun WebSocketExtensionsConfig.installDeflate()

expect fun HttpClientF(
    block: HttpClientConfig<*>.() -> Unit = {}
): HttpClient