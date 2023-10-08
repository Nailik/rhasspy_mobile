package org.rhasspy.mobile.data.service

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.viewstate.TextWrapper
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperStableStringResource
import org.rhasspy.mobile.data.viewstate.TextWrapper.TextWrapperString

@Stable
sealed class ConnectionState {

    data object Disabled : ConnectionState()
    data object Loading : ConnectionState()

    data object Success : ConnectionState()

    data class ErrorState(val message: TextWrapper) : ConnectionState() {

        constructor(text: String) : this(TextWrapperString(text))
        constructor(resource: StableStringResource) : this(TextWrapperStableStringResource(resource))
        constructor(exception: Exception) : this(TextWrapperString("${exception.message ?: exception}"))

    }

}