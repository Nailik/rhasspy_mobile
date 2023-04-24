package org.rhasspy.mobile.platformspecific.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.platformspecific.application.NativeApplication

/**
 * to check microphone permission
 */
actual object MicrophonePermission : KoinComponent {

    private val context by inject<NativeApplication>()

    /**
     * to observe if microphone permission is granted
     */
    private val _granted = MutableStateFlow(isGranted())
    actual val granted: StateFlow<Boolean> = _granted

    /**
     * to check if the information dialog should be shown
     */
    actual fun requestPermissionExternally() {
        context.startActivity(
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
        return context.currentActivity?.let {
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
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * read from system
     */
    actual fun update() {
        _granted.value = isGranted()
    }

}