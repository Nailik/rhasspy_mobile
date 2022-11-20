package org.rhasspy.mobile.services

abstract class ServiceResponse<T> {

    data class Success<T>(val data: T) : ServiceResponse<T>()

    data class Error<T>(val exception: Exception) : ServiceResponse<T>()

    class NotInitialized<T> : ServiceResponse<T>()

    class Disabled: ServiceResponse<Unit>()

    class Nothing: ServiceResponse<Unit>()

}