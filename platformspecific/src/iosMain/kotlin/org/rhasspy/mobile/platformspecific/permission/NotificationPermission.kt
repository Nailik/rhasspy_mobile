package org.rhasspy.mobile.platformspecific.permission

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest

internal actual class NotificationPermission actual constructor(
    private val nativeApplication: NativeApplication,
    private val externalResultRequest: IExternalResultRequest,
) : INotificationPermission {

    /**
     * to observe if notification permission is granted
     */
    actual override val granted: StateFlow<Boolean>
        get() = MutableStateFlow(true) //TODO("Not yet implemented")

    /**
     * to check if the information dialog should be shown
     */
    actual override fun shouldShowInformationDialog(): Boolean {
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
     * request permission from user
     */
    actual override suspend fun request() {
        //TODO("Not yet implemented")
    }

}