package org.rhasspy.mobile.services.httpclient

abstract class HttpClientResponse<T> {

    data class Success<T>(val data: T) : HttpClientResponse<T>()

    data class Error<T>(val exception: Exception) : HttpClientResponse<T>()

    class NotInitialized<T> : HttpClientResponse<T>()

}