package org.rhasspy.mobile.platformspecific.permission

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest

interface IOverlayPermission {

    val granted: StateFlow<Boolean>
    fun request(): Boolean
    fun isGranted(): Boolean
    fun update()

}

internal expect class OverlayPermission(
    nativeApplication: NativeApplication,
    externalResultRequest: IExternalResultRequest
) : IOverlayPermission {

    /**
     * to observe if microphone permission is granted
     */
    override val granted: StateFlow<Boolean>

    /**
     * to request the permission externally, redirect user to settings
     */
    override fun request(): Boolean

    /**
     * check if the permission is currently granted
     */
    override fun isGranted(): Boolean

    /**
     * read from system
     */
    override fun update()

}