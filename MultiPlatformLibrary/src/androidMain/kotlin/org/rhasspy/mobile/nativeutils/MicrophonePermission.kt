package org.rhasspy.mobile.nativeutils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.Application

actual object MicrophonePermission {

    private val status = MutableStateFlow(isGranted())

    private var launcher: ActivityResultLauncher<String>? = null

    actual val granted: StateFlow<Boolean> get() = status

    private var onResultCallback: ((Boolean) -> Unit)? = null

    fun init(activity: ComponentActivity) {
        launcher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission(), callback)
    }

    private val callback = ActivityResultCallback<Boolean> {
        //not using boolean because it doesn't tell us if permission may be always denied
        status.value = it
        onResultCallback?.invoke(it)
        onResultCallback = null
    }

    fun requestPermission(redirect: Boolean, onResult: (granted: Boolean) -> Unit) {
        if (status.value) {
            onResult.invoke(true)
            return
        }

        onResultCallback = onResult
        if (redirect) {
            Application.Instance.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:org.rhasspy.mobile.android")
                ).also {
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        } else {
            launcher?.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun shouldShowInfoDialog(activity: ComponentActivity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)
    }

    private fun isGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(Application.Instance, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

}