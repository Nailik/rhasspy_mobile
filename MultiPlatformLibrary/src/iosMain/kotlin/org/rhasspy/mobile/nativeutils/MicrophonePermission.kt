package org.rhasspy.mobile.nativeutils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual object MicrophonePermission {

    actual val granted: StateFlow<Boolean>
        get() = MutableStateFlow(true)

}