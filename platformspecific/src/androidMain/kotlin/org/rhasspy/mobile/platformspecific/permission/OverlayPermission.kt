package org.rhasspy.mobile.platformspecific.permission

import android.provider.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequest
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention

actual class OverlayPermission actual constructor(
    private val nativeApplication: NativeApplication
) {

    private val _granted = MutableStateFlow(isGranted())

    /**
     * to observe if microphone permission is granted
     */
    actual val granted: StateFlow<Boolean> = _granted

    /**
     * to request the permission externally, redirect user to settings
     */
    actual fun requestPermission(): Boolean {
        val result = ExternalResultRequest.launch(ExternalResultRequestIntention.OpenOverlaySettings)

        return if (result is ExternalRedirectResult.Success) {
            true
        } else {
            return ExternalResultRequest.launch(ExternalResultRequestIntention.OpenAppSettings) is ExternalRedirectResult.Success
        }
    }

    /**
     * check if the permission is currently granted
     */
    actual fun isGranted(): Boolean = Settings.canDrawOverlays(nativeApplication)

    /**
     * read from system
     */
    actual fun update() {
        _granted.value = isGranted()
    }

}