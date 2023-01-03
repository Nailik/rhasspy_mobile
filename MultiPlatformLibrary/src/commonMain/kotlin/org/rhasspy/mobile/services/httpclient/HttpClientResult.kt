package org.rhasspy.mobile.services.httpclient

sealed class HttpClientResult<T> {

    class Success<T>(val data: T) : HttpClientResult<T>()

    class Error<T>(val exception: Exception) : HttpClientResult<T>()

}