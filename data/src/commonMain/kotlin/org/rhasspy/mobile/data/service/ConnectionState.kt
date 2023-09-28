package org.rhasspy.mobile.data.service

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.viewstate.TextWrapper
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperStableStringResource
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperString
import org.rhasspy.mobile.resources.MR

@Stable
sealed class ConnectionState {

    data object Pending : ConnectionState()

    data object Loading : ConnectionState()

    data object Success : ConnectionState()

    sealed class ErrorState : ConnectionState() {

        class Exception(val exception: kotlin.Exception? = null) : ErrorState()

        class Error(val information: StableStringResource) : ErrorState()

    }

    data object Disabled : ConnectionState()

    fun getText(): TextWrapper {
        return when (this) {
            Disabled      -> TextWrapperStableStringResource(MR.strings.disabled.stable)
            is ErrorState -> when (this) {
                is ErrorState.Error     -> TextWrapperStableStringResource(this.information)
                is ErrorState.Exception -> this.exception?.message?.let { TextWrapperString(it) } ?: TextWrapperStableStringResource(MR.strings.error.stable)
            }

            Loading       -> TextWrapperStableStringResource(MR.strings.loading.stable)
            Pending       -> TextWrapperStableStringResource(MR.strings.pending.stable)
            Success       -> TextWrapperStableStringResource(MR.strings.success.stable)
        }
    }

}