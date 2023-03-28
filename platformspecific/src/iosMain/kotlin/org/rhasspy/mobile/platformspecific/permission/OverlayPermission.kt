package org.rhasspy.mobile.platformspecific.permission

import kotlinx.coroutines.flow.StateFlow

actual object OverlayPermission {
    actual val granted: StateFlow<Boolean>
        get() = TODO("Not yet implemented")

    actual fun requestPermission(onGranted: () -> Unit) {
    }

    actual fun isGranted(): Boolean {
        TODO("Not yet implemented")
    }
}