package org.rhasspy.mobile.logic.nativeutils

import kotlinx.coroutines.flow.StateFlow

/**
 * to check microphone permission
 */
actual object MicrophonePermission {

    /**
     * to observe if microphone permission is granted
     */
    actual val granted: StateFlow<Boolean>
        get() = TODO("Not yet implemented")

    /**
     * to check if the information dialog should be shown
     */
    actual fun shouldShowInformationDialog(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * to request the permission externally, redirect user to settings
     */
    actual fun requestPermissionExternally() {
    }

}