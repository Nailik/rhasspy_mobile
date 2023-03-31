package org.rhasspy.mobile.logic.nativeutils

import io.ktor.client.engine.cio.CIOEngineConfig

expect fun CIOEngineConfig.configureEngine(isHttpVerificationDisabled: Boolean)