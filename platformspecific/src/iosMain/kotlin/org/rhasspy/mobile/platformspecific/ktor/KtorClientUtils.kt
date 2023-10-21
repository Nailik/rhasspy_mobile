package org.rhasspy.mobile.platformspecific.ktor

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.websocket.WebSocketExtensionsConfig

/**
 * configure client engine
 */
actual fun CIOEngineConfig.configureEngine(isHttpVerificationDisabled: Boolean) {
    //TODO #515
}

actual fun WebSocketExtensionsConfig.installDeflate() {
    //TODO #515
}

actual fun getEngine(): HttpClientEngineFactory<*> = CIO