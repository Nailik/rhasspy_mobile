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
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.map
import org.rhasspy.mobile.Application

actual object MicrophonePermission {

    private val status = MutableLiveData(isGranted())

    private var launcher: ActivityResultLauncher<String>? = null

    actual val granted: LiveData<Boolean> = status.map { it }

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
        if (granted.value) {
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