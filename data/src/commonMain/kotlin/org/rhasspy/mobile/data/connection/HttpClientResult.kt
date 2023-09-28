package org.rhasspy.mobile.data.connection

import org.rhasspy.mobile.data.connection.HttpClientResult.HttpClientError.KnownError
import org.rhasspy.mobile.data.connection.HttpClientResult.HttpClientError.UnknownError
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.data.service.ConnectionState.ErrorState
import org.rhasspy.mobile.data.service.ConnectionState.Success

sealed class HttpClientResult<T> {

    class Success<T>(val data: T) : HttpClientResult<T>()

    sealed class HttpClientError<T> : HttpClientResult<T>() {

        class UnknownError<T>(val exception: Exception) : HttpClientError<T>()

        class KnownError<T>(val exception: HttpClientErrorType) : HttpClientError<T>()

    }


    fun toServiceState(): ConnectionState {
        return when (this) {
            is UnknownError -> ErrorState.Exception(this.exception)
            is Success      -> Success
            is KnownError   -> ErrorState.Error(this.exception.text)
        }
    }

}