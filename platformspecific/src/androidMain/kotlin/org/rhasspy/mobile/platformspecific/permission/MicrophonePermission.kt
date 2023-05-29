package org.rhasspy.mobile.platformspecific.permission

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequest

/**
 * to check microphone permission
 */
actual class MicrophonePermission actual constructor(
    private val nativeApplication: NativeApplication,
    private val externalResultRequest: ExternalResultRequest
) {

    /**
     * to observe if microphone permission is granted
     */
    private val _granted = MutableStateFlow(isGranted())
    actual val granted: StateFlow<Boolean> = _granted

    /**
     * to request the permission externally, redirect user to settings
     */
    actual fun shouldShowInformationDialog(): Boolean {
        return nativeApplication.currentActivity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(
                it,
                Manifest.permission.RECORD_AUDIO
            )
        } ?: true
    }

    /**
     * check if permission is granted
     */
    private fun isGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            nativeApplication,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * read from system
     */
    actual fun update() {
        _granted.value = isGranted()
    }

    actual suspend fun request() {
        _granted.value = externalResultRequest.launchForPermission(Manifest.permission.RECORD_AUDIO)
    }

}