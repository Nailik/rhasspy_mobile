package org.rhasspy.mobile.services.httpclient

import org.rhasspy.mobile.middleware.ServiceState

sealed class HttpClientResult<T> {

    class Success<T>(val data: T) : HttpClientResult<T>()

    class Error<T>(val exception: Exception) : HttpClientResult<T>()

    fun toServiceState(): ServiceState {
        return when (this) {
            is Error -> ServiceState.Exception(this.exception)
            is Success -> ServiceState.Success
        }
    }

}