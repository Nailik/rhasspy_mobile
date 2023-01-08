package org.rhasspy.mobile.middleware

import dev.icerock.moko.resources.StringResource

sealed class ServiceState {

    object Pending : ServiceState()

    object Loading : ServiceState()

    object Success : ServiceState()

    class Warning(val information: StringResource) : ServiceState()

    class Exception(val exception: kotlin.Exception? = null) : ServiceState()

    class Error(val information: StringResource) : ServiceState()

    object Disabled : ServiceState()

}