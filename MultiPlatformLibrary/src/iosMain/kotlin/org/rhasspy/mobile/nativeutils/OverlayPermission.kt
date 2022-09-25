package org.rhasspy.mobile.nativeutils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual object OverlayPermission {

    actual val granted: StateFlow<Boolean>
        get() = MutableStateFlow(true)

    actual fun isGranted(): Boolean {
        TODO("Not yet implemented")
    }

}