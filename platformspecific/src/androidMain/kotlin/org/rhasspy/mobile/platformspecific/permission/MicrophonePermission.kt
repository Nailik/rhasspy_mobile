package org.rhasspy.mobile.platformspecific.permission

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest

/**
 * to check microphone permission
 */
internal actual class MicrophonePermission actual constructor(
    private val nativeApplication: NativeApplication,
    private val externalResultRequest: IExternalResultRequest
) : IMicrophonePermission {

    /**
     * to observe if microphone permission is granted
     */
    private val _granted = MutableStateFlow(isGranted())
    actual override val granted: StateFlow<Boolean> = _granted

    /**
     * to request the permission externally, redirect user to settings
     */
    actual override fun shouldShowInformationDialog(): Boolean {
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
    actual override fun update() {
        _granted.value = isGranted()
    }

    actual override suspend fun request() {
        _granted.value = externalResultRequest.launchForPermission(Manifest.permission.RECORD_AUDIO)
    }

}