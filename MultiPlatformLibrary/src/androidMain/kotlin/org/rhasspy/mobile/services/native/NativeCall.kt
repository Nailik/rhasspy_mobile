package org.rhasspy.mobile.services.native

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*

actual class NativeCall {

    lateinit var applicationCall: ApplicationCall

    fun initialize(call: ApplicationCall): NativeCall {
        applicationCall = call
        return this
    }

    actual suspend inline fun <reified T : Any> receive(): T = applicationCall.receive()

    actual fun requestContentType(): ContentType {
        return applicationCall.request.contentType()
    }

    actual suspend fun respondBytes(bytes: ByteArray, contentType: ContentType) {
        applicationCall.respondBytes(bytes, contentType)
    }

}