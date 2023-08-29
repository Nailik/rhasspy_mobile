package org.rhasspy.mobile.data.connection

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource

@Stable
sealed interface ConnectionState {

    data object Connecting : ConnectionState

    data object Reconnecting : ConnectionState

    data object Disconnected : ConnectionState

    data object Connected : ConnectionState

    data object Disabled : ConnectionState

    class Exception(val exception: kotlin.Exception? = null) : ConnectionState

    class Error(val information: StableStringResource) : ConnectionState

}