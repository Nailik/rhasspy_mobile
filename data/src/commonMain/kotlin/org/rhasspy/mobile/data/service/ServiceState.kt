package org.rhasspy.mobile.data.service

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.resources.MR

@Stable
sealed class ServiceState {

    data object Pending : ServiceState()

    data object Loading : ServiceState()

    data object Success : ServiceState()

    class Exception(val exception: kotlin.Exception? = null) : ServiceState()

    class Error(val information: StableStringResource) : ServiceState()

    data object Disabled : ServiceState()

    fun isOpenServiceStateDialogEnabled(): Boolean = (this is Exception || this is Error)

    fun getText(): Any {
        return when (this) {
            Disabled     -> MR.strings.disabled
            is Error     -> this.information
            is Exception -> this.exception?.message ?: MR.strings.error
            Loading      -> MR.strings.loading
            Pending      -> MR.strings.pending
            Success      -> MR.strings.success
        }
    }

}