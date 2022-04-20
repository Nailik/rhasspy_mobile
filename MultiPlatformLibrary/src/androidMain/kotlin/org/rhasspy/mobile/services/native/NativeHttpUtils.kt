package org.rhasspy.mobile.services.native

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*

actual fun Application.installCompression() {
    install(Compression) {
        gzip()
    }
}

actual fun Application.installCallLogging() {
    install(CallLogging)
}