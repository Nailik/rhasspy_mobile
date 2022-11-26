package org.rhasspy.mobile.middleware

sealed class EventState(val information: String? = null) {

    object Pending : EventState()

    object Loading : EventState()

    class Success(information: String? = null) : EventState(information)

    class Warning(information: String? = null) : EventState(information)

    class Error(private val errorType: ErrorType? = null) : EventState("") {
        override fun toString(): String {
            return errorType?.toString() ?: ""
        }
    }

}