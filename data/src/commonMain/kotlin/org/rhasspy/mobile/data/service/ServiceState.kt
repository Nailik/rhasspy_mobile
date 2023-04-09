package org.rhasspy.mobile.data.service

import org.rhasspy.mobile.data.resource.StableStringResource

sealed class ServiceState {

    object Pending : ServiceState()

    object Loading : ServiceState()

    object Success : ServiceState()

    class Exception(val exception: kotlin.Exception? = null) : ServiceState()

    class Error(val information: StableStringResource) : ServiceState()

    object Disabled : ServiceState()

}