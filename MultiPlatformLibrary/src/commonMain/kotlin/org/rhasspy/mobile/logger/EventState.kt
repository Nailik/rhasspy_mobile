package org.rhasspy.mobile.logger

sealed class EventState(val information: String? = null) {

    object Pending : EventState()

    object Loading : EventState()

    class Success(information: String? = null) : EventState(information)

    class Warning(information: String? = null) : EventState(information)

    class Error(information: String? = null) : EventState(information)

}