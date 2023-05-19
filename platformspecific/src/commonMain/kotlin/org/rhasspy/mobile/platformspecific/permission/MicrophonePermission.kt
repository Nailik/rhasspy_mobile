package org.rhasspy.mobile.platformspecific.permission

import kotlinx.coroutines.flow.StateFlow

/**
 * to check microphone permission
 */
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
     * read from system
     */
    fun update()

    /**
     * request permission from user
     */
    suspend fun request()

}