package org.rhasspy.mobile.platformspecific.permission

import kotlinx.coroutines.flow.StateFlow

expect object OverlayPermission {

    /**
     * to observe if microphone permission is granted
     */
    val granted: StateFlow<Boolean>

    /**
     * to request the permission externally, redirect user to settings
     */
    fun requestPermission(onGranted: () -> Unit)

    /**
     * check if the permission is currently granted
     */
    fun isGranted(): Boolean

    /**
     * read from system
     */
    fun update()

}