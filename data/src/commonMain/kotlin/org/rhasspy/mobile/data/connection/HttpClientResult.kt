package org.rhasspy.mobile.data.connection

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.data.service.ConnectionState.ErrorState
import org.rhasspy.mobile.data.service.ConnectionState.Success
import org.rhasspy.mobile.data.viewstate.TextWrapper

sealed class HttpClientResult<T> {

    data class Success<T>(val data: T) : HttpClientResult<T>()

    data class HttpClientError<T>(val message: TextWrapper) : HttpClientResult<T>() {

        constructor(text: String) : this(TextWrapper.TextWrapperString(text))
        constructor(resource: StableStringResource) : this(TextWrapper.TextWrapperStableStringResource(resource))
        constructor(exception: Exception) : this(TextWrapper.TextWrapperString("$exception ${exception.message}"))

        fun <X> toType(): HttpClientError<X> {
            return HttpClientError(message)
        }

    }


    fun toConnectionState(): ConnectionState {
        return when (this) {
            is HttpClientError -> ErrorState(this.message)
            is Success         -> Success
        }
    }

}