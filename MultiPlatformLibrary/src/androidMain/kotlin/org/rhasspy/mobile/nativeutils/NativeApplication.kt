package org.rhasspy.mobile.nativeutils

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.AppActivity

actual abstract class NativeApplication : MultiDexApplication() {

    var currentActivity: AppActivity? = null
        private set

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_START -> currentlyAppInBackground.value = false
                    Lifecycle.Event.ON_STOP -> currentlyAppInBackground.value = true
                    else -> {}
                }
            }
        })
    }

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {}
            override fun onActivityStarted(p0: Activity) {
                //always represents top activity
                if (p0 is AppActivity) {
                    currentActivity = p0
                }
            }

            override fun onActivityResumed(p0: Activity) {
                MicrophonePermission.update()
                OverlayPermission.update()
            }

            override fun onActivityPaused(p0: Activity) {}
            override fun onActivityStopped(p0: Activity) {}
            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
            override fun onActivityDestroyed(p0: Activity) {}
        })
    }

    actual open fun restart() {}
    actual val currentlyAppInBackground = MutableStateFlow(false)
    actual val isAppInBackground: StateFlow<Boolean>
        get() = currentlyAppInBackground

    actual abstract suspend fun updateWidgetNative()

}