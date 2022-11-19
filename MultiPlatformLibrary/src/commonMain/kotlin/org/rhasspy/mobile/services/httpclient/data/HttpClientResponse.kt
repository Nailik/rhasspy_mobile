package org.rhasspy.mobile.services.httpclient.data

abstract class HttpClientResponse<T>(
    open val callType: HttpClientCallType
) {
    data class HttpClientError<T>(
        val e: Exception,
        override val callType: HttpClientCallType
    ) : HttpClientResponse<T>(callType)

    data class HttpClientSuccess<T>(
        val response: T,
        override val callType: HttpClientCallType
    ) : HttpClientResponse<T>(callType)
}