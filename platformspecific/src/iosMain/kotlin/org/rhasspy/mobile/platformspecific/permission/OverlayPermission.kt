package org.rhasspy.mobile.platformspecific.permission

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest

internal actual class OverlayPermission actual constructor(
    private val nativeApplication: NativeApplication,
    private val externalResultRequest: IExternalResultRequest,
) : IOverlayPermission {

    /**
     * to observe if microphone permission is granted
     */
    actual override val granted: StateFlow<Boolean>
        get() = MutableStateFlow(true) //TODO("Not yet implemented")

    /**
     * check if the permission is currently granted
     */
    actual override fun isGranted(): Boolean {
        //TODO("Not yet implemented")
        return true
    }

    /**
     * read from system
     */
    actual override fun update() {
        //TODO("Not yet implemented")
    }

    /**
     * to request the permission externally, redirect user to settings
     */
    actual override fun request(): Boolean {
        //TODO("Not yet implemented")
        return true
    }

}