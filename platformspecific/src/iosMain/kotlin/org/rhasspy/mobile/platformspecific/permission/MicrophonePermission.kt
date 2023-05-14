package org.rhasspy.mobile.platformspecific.permission

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual object MicrophonePermission {

    /**
     * to observe if microphone permission is granted
     */
    actual val granted: StateFlow<Boolean>
        get() = MutableStateFlow(true) //TODO("Not yet implemented")

    /**
     * to check if the information dialog should be shown
     */
    actual fun shouldShowInformationDialog(): Boolean {
        //TODO("Not yet implemented")
        return true
    }

    /**
     * read from system
     */
    actual fun update() {
        //TODO("Not yet implemented")
    }
}