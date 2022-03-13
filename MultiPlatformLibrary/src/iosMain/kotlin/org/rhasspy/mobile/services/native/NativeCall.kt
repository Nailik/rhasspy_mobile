package org.rhasspy.mobile.services.native

import io.ktor.http.*

actual class NativeCall {

    actual suspend inline fun <reified T : Any> receive(): T {
        TODO("Not yet implemented")
    }

    actual fun requestContentType(): ContentType {
        TODO("Not yet implemented")
    }

    actual suspend fun respondBytes(bytes: ByteArray, contentType: ContentType) {
        TODO("Not yet implemented")
    }


}