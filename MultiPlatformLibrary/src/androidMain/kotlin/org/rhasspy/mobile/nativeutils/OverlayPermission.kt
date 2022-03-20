package org.rhasspy.mobile.nativeutils

import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import org.rhasspy.mobile.Application

actual object OverlayPermission {

    private val status = MutableLiveData(isGranted())

    actual val granted: LiveData<Boolean> = status.readOnly()

    private var onResultCallback: ((Boolean) -> Unit)? = null

    fun init(activity: ComponentActivity) {
        activity.lifecycle.addObserver(object : LifecycleObserver {
            @Suppress("unused")
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                status.value = isGranted()
                onResultCallback?.invoke(status.value)
            }
        })
    }

    fun requestPermission(onResult: (granted: Boolean) -> Unit) {
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

    private fun isGranted(): Boolean {
        return Settings.canDrawOverlays(Application.Instance)
    }

}