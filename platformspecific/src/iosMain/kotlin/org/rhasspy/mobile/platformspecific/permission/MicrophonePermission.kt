package org.rhasspy.mobile.platformspecific.permission

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual class MicrophonePermission actual constructor(
    private val nativeApplication: NativeApplication
) {

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

    /**
     * request permission from user
     */
    actual suspend fun request() {
        //TODO("Not yet implemented")
    }

}