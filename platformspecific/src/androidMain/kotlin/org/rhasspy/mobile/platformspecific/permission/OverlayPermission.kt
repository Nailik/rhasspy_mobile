package org.rhasspy.mobile.platformspecific.permission

import android.provider.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequest
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention

actual object OverlayPermission : KoinComponent {

    private val context by inject<NativeApplication>()
    private val _granted = MutableStateFlow(isGranted())

    /**
     * to observe if microphone permission is granted
     */
    actual val granted: StateFlow<Boolean> = _granted

    private var onGranted: (() -> Unit)? = null

    /**
     * to request the permission externally, redirect user to settings
     */
    actual fun requestPermission(onGranted: () -> Unit): Boolean {
        OverlayPermission.onGranted = onGranted

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
    actual fun isGranted(): Boolean = Settings.canDrawOverlays(context)

    /**
     * read from system
     */
    actual fun update() {
        _granted.value = isGranted()
        if (_granted.value) {
            onGranted?.invoke()
        }
        onGranted = null
    }

}