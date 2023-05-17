package org.rhasspy.mobile.platformspecific.application

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.external.ExternalRedirect

actual abstract class NativeApplication : MultiDexApplication() {

    var currentActivity: AppCompatActivity? = null
        private set

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_START -> currentlyAppInBackground.value = false
                    Lifecycle.Event.ON_STOP -> currentlyAppInBackground.value = true
                    Lifecycle.Event.ON_RESUME -> resume()
                    else -> {}
                }
            }
        })
    }

    actual override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {}
            override fun onActivityStarted(p0: Activity) {
                //always represents top activity
                if (p0 is AppCompatActivity) {
                    currentActivity = p0
                    ExternalRedirect.registerCallback(p0)
                }
            }

            override fun onActivityResumed(p0: Activity) {}
            override fun onActivityPaused(p0: Activity) {}
            override fun onActivityStopped(p0: Activity) {}
            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
            override fun onActivityDestroyed(p0: Activity) {}
        })
    }

    actual val currentlyAppInBackground = MutableStateFlow(false)
    actual val isAppInBackground: StateFlow<Boolean>
        get() = currentlyAppInBackground

    actual fun isInstrumentedTest(): Boolean {
        return Settings.System.getString(contentResolver, "firebase.test.lab") == "true"
    }

    actual fun restart() {
        try {
            val packageManager: PackageManager = this.packageManager
            val intent: Intent = packageManager.getLaunchIntentForPackage(this.packageName)!!
            val componentName: ComponentName = intent.component!!
            val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
            this.startActivity(restartIntent)
            Runtime.getRuntime().exit(0)
        } catch (e: Exception) {
            Runtime.getRuntime().exit(0)
        }
    }

    actual abstract fun setCrashlyticsCollectionEnabled(enabled: Boolean)
    actual abstract val isHasStarted: StateFlow<Boolean>
    actual abstract fun resume()
    actual abstract fun startRecordingAction()
    actual fun closeApp() {
        currentActivity?.moveTaskToBack(false)
    }

}