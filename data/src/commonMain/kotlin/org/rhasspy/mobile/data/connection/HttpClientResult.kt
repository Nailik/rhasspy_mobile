package org.rhasspy.mobile.data.connection

import org.rhasspy.mobile.data.service.ServiceState

sealed class HttpClientResult<T> {

    class Success<T>(val data: T) : HttpClientResult<T>()

    class Error<T>(val exception: Exception) : HttpClientResult<T>()

    class KnownError<T>(val exception: HttpClientErrorType) : HttpClientResult<T>()

    fun toServiceState(): ServiceState {
        return when (this) {
            is Error   -> ServiceState.Exception(this.exception)
            is Success -> ServiceState.Success
            is KnownError -> ServiceState.Error(this.exception.text)
        }
    }

}