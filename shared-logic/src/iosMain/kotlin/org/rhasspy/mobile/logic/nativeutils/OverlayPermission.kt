package org.rhasspy.mobile.logic.nativeutils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual object OverlayPermission {

    actual val granted: StateFlow<Boolean>
        get() = MutableStateFlow(true)

    actual fun isGranted(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun requestPermission(onGranted: () -> Unit) {
        TODO("Not yet implemented")
    }

}