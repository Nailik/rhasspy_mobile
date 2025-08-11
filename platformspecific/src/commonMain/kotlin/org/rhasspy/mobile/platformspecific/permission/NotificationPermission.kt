package org.rhasspy.mobile.platformspecific.permission

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest

interface INotificationPermission {

    val granted: StateFlow<Boolean>

    fun shouldShowInformationDialog(): Boolean
    fun update()
    suspend fun request()

}

/**
 * to check microphone permission
 */
internal expect class NotificationPermission(
    nativeApplication: NativeApplication,
    externalResultRequest: IExternalResultRequest,
) : INotificationPermission {

    /**
     * to observe if notification permission is granted
     */
    override val granted: StateFlow<Boolean>

    /**
     * to check if the information dialog should be shown
     */
    override fun shouldShowInformationDialog(): Boolean

    /**
     * read from system
     */
    override fun update()

    /**
     * request permission from user
     */
    override suspend fun request()

}