package org.rhasspy.mobile.services.httpclient.data

import io.ktor.client.statement.*

abstract class HttpClientResponse(
    open val callType: HttpClientCallType
) {
    data class HttpClientError(
        val e: Exception,
        override val callType: HttpClientCallType
    ) : HttpClientResponse(callType)

    data class HttpClientSuccess(
        val response: Any?,
        override val callType: HttpClientCallType
    ) : HttpClientResponse(callType)
}