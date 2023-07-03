package org.rhasspy.mobile.platformspecific.permission

import android.provider.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest

internal actual class OverlayPermission actual constructor(
    private val nativeApplication: INativeApplication,
    private val externalResultRequest: IExternalResultRequest
) : IOverlayPermission {

    private val _granted = MutableStateFlow(isGranted())

    /**
     * to observe if microphone permission is granted
     */
    actual override val granted: StateFlow<Boolean> = _granted

    /**
     * to request the permission externally, redirect user to settings
     */
    actual override fun request(): Boolean {
        val result = externalResultRequest.launch(ExternalResultRequestIntention.OpenOverlaySettings)

        return if (result is ExternalRedirectResult.Success) {
            true
        } else {
            return externalResultRequest.launch(ExternalResultRequestIntention.OpenAppSettings) is ExternalRedirectResult.Success
        }
    }

    /**
     * check if the permission is currently granted
     */
    actual override fun isGranted(): Boolean = Settings.canDrawOverlays(nativeApplication as NativeApplication)

    /**
     * read from system
     */
    actual override fun update() {
        _granted.value = isGranted()
    }

}