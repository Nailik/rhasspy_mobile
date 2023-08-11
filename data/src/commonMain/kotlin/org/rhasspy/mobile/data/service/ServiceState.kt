package org.rhasspy.mobile.data.service

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.viewstate.TextWrapper
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperStableStringResource
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperString
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

    fun getText(): TextWrapper {
        return when (this) {
            Disabled     -> TextWrapperStableStringResource(MR.strings.disabled.stable)
            is Error     -> TextWrapperStableStringResource(this.information)
            is Exception -> this.exception?.message?.let { TextWrapperString(it) } ?: TextWrapperStableStringResource(MR.strings.error.stable)
            Loading      -> TextWrapperStableStringResource(MR.strings.loading.stable)
            Pending      -> TextWrapperStableStringResource(MR.strings.pending.stable)
            Success      -> TextWrapperStableStringResource(MR.strings.success.stable)
        }
    }

}