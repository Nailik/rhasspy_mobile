package org.rhasspy.mobile.nativeutils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.readOnly

/**
 * to check microphone permission
 */
actual object MicrophonePermission {

    /**
     * to observe if microphone permission is granted
     */
    private val _granted = MutableStateFlow(isGranted())
    actual val granted = _granted.readOnly

    /**
     * update the permission on initialization of app
     */
    fun update() {
        _granted.value = isGranted()
    }

    /**
     * to check if the information dialog should be shown
     */
    actual fun requestPermissionExternally() {
        Application.nativeInstance.startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:org.rhasspy.mobile.android")
            ).also {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    /**
     * to request the permission externally, redirect user to settings
     */
    actual fun shouldShowInformationDialog(): Boolean {
        return Application.nativeInstance.currentActivity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(
                it,
                Manifest.permission.RECORD_AUDIO
            )
        } ?: run {
            true
        }
    }

    /**
     * check if permission is granted
     */
    private fun isGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            Application.nativeInstance,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

}