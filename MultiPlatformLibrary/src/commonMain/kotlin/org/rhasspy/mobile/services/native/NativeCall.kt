package org.rhasspy.mobile.services.native

import io.ktor.http.*

expect class NativeCall {

    suspend inline fun <reified T : Any> receive(): T

    fun requestContentType(): ContentType

    suspend fun respondBytes(bytes: ByteArray, contentType: ContentType)

}