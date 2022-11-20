package org.rhasspy.mobile.services

abstract class ServiceResponse<T> {

    data class Success<T>(val data: T) : ServiceResponse<T>()

    data class Error(val exception: Exception) : ServiceResponse<Unit>()

    class NotInitialized : ServiceResponse<Unit>()

    class Disabled: ServiceResponse<Unit>()

    class Nothing: ServiceResponse<Unit>()

}