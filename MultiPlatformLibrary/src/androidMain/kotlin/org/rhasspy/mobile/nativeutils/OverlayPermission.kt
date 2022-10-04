package org.rhasspy.mobile.nativeutils

import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.Application

actual object OverlayPermission {

    private val status = MutableStateFlow(isGranted())

    actual val granted: StateFlow<Boolean> get() = status

    private var onResultCallback: ((Boolean) -> Unit)? = null

    fun init(activity: ComponentActivity) {
        activity.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    status.value = isGranted()
                    onResultCallback?.invoke(status.value)
                }
            }
        })
    }

    actual fun requestPermission(onResult: (granted: Boolean) -> Unit) {
        if (status.value) {
            onResult.invoke(true)
            return
        }

        onResultCallback = onResult
        // send user to the device settings
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        Application.Instance.startActivity(intent)
    }

    actual fun isGranted(): Boolean {
        return Settings.canDrawOverlays(Application.Instance)
    }

}