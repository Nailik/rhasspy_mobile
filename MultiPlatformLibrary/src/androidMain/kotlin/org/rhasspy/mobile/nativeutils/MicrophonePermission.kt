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

actual object MicrophonePermission {

    private val _granted = MutableStateFlow(isGranted())
    actual val granted = _granted.readOnly

    fun update() {
        _granted.value = isGranted()
    }

    actual fun requestPermissionExternally() {
        Application.Instance.startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:org.rhasspy.mobile.android")
            ).also {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    private fun isGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            Application.Instance,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    actual fun shouldShowInformationDialog(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            Application.Instance.currentActivity!!,
            Manifest.permission.RECORD_AUDIO
        )
    }

}