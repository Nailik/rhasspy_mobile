package org.rhasspy.mobile.data.service

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource

@Stable
sealed class ServiceState {

    object Pending : ServiceState()

    object Loading : ServiceState()

    object Success : ServiceState()

    class Exception(val exception: kotlin.Exception? = null) : ServiceState()

    class Error(val information: StableStringResource) : ServiceState()

    object Disabled : ServiceState()

    fun isOpenServiceStateDialogEnabled(): Boolean = (this is ServiceState.Exception || this is ServiceState.Error)
    fun ServiceState.getDialogText(): Any = when (this) {
        is ServiceState.Error -> this.information
        is ServiceState.Exception -> this.exception?.toString() ?: ""
        else -> ""
    }

}