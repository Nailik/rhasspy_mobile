package org.rhasspy.mobile.platformspecific.permission

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual class OverlayPermission actual constructor(
    private val nativeApplication: NativeApplication
) {

    /**
     * to observe if microphone permission is granted
     */
    actual val granted: StateFlow<Boolean>
        get() = MutableStateFlow(true) //TODO("Not yet implemented")

    /**
     * check if the permission is currently granted
     */
    actual fun isGranted(): Boolean {
        //TODO("Not yet implemented")
        return true
    }

    /**
     * read from system
     */
    actual fun update() {
        //TODO("Not yet implemented")
    }

    /**
     * to request the permission externally, redirect user to settings
     */
    actual fun request(): Boolean {
        //TODO("Not yet implemented")
        return true
    }

}