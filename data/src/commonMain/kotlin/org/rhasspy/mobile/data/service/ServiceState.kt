package org.rhasspy.mobile.data.service

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource

@Stable
sealed class ServiceState {

    data object Pending : ServiceState()

    data object Loading : ServiceState()

    data object Success : ServiceState()

    class Exception(val exception: kotlin.Exception? = null) : ServiceState()

    class Error(val information: StableStringResource) : ServiceState()

    data object Disabled : ServiceState()

    fun isOpenServiceStateDialogEnabled(): Boolean = (this is Exception || this is Error)

}