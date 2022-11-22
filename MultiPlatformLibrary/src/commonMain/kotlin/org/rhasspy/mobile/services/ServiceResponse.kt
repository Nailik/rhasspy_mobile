package org.rhasspy.mobile.services

sealed class ServiceResponse<T> {

    data class Success<T>(val data: T) : ServiceResponse<T>()

    data class Error(val error: Exception) : ServiceResponse<Unit>()

    object NotInitialized : ServiceResponse<Unit>()

    object Disabled: ServiceResponse<Unit>()

    object Nothing: ServiceResponse<Unit>()

}