package org.rhasspy.mobile.platformspecific.ktor

import io.ktor.client.engine.cio.CIOEngineConfig

expect fun CIOEngineConfig.configureEngine(isHttpVerificationDisabled: Boolean)