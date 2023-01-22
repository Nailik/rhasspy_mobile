package org.rhasspy.mobile.nativeutils

import kotlinx.coroutines.flow.StateFlow

/**
 * to check microphone permission
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MicrophonePermission {

    /**
     * to observe if microphone permission is granted
     */
    val granted: StateFlow<Boolean>

    /**
     * to check if the information dialog should be shown
     */
    fun shouldShowInformationDialog(): Boolean

    /**
     * to request the permission externally, redirect user to settings
     */
    fun requestPermissionExternally()

}