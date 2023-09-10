package org.rhasspy.mobile.platformspecific.permission

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest

internal actual class MicrophonePermission actual constructor(
    private val nativeApplication: NativeApplication,
    private val externalResultRequest: IExternalResultRequest
) : IMicrophonePermission {

    /**
     * to observe if microphone permission is granted
     */
    actual override val granted: StateFlow<Boolean>
        get() = MutableStateFlow(true) //TODO #260

    /**
     * to check if the information dialog should be shown
     */
    actual override fun shouldShowInformationDialog(): Boolean {
        //TODO #260
        return true
    }

    /**
     * read from system
     */
    actual override fun update() {
        //TODO #260
    }

    /**
     * request permission from user
     */
    actual override suspend fun request() {
        //TODO #260
    }

}