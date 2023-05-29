package org.rhasspy.mobile.platformspecific.permission

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequest

expect class OverlayPermission(
    nativeApplication: NativeApplication,
    externalResultRequest: ExternalResultRequest
) {

    /**
     * to observe if microphone permission is granted
     */
    val granted: StateFlow<Boolean>

    /**
     * to request the permission externally, redirect user to settings
     */
    fun request(): Boolean

    /**
     * check if the permission is currently granted
     */
    fun isGranted(): Boolean

    /**
     * read from system
     */
    fun update()

}