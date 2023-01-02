package org.rhasspy.mobile.middleware

sealed class ServiceState(val information: String? = null) {

    object Pending : ServiceState()

    object Loading : ServiceState()

    class Success(information: String? = null) : ServiceState(information)

    class Warning(information: String? = null) : ServiceState(information)

    class Error : ServiceState()

    object Disabled : ServiceState()

}