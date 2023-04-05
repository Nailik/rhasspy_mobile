package org.rhasspy.mobile.platformspecific.permission

import kotlinx.coroutines.flow.StateFlow

actual object OverlayPermission {

    /**
     * to observe if microphone permission is granted
     */
    actual val granted: StateFlow<Boolean>
        get() = TODO("Not yet implemented")

    /**
     * to request the permission externally, redirect user to settings
     */
    actual fun requestPermission(onGranted: () -> Unit) {
        TODO("Not yet implemented")
    }

    /**
     * check if the permission is currently granted
     */
    actual fun isGranted(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * read from system
     */
    actual fun update() {
        TODO("Not yet implemented")
    }
}