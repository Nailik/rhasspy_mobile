package org.rhasspy.mobile.data.service

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.viewstate.TextWrapper
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperStableStringResource
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperString

@Stable
sealed class ConnectionState {

    data object Disabled : ConnectionState()

    data object Success : ConnectionState()

    sealed class ErrorState : ConnectionState() {

        class Exception(val exception: kotlin.Exception) : ErrorState()

        class Error(val information: StableStringResource) : ErrorState()

        fun getText(): TextWrapper {
            return when (this) {
                is Error     -> TextWrapperStableStringResource(this.information)
                is Exception -> TextWrapperString(this.exception.message ?: this.exception.toString())
            }
        }
    }

}