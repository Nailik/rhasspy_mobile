package org.rhasspy.mobile

import android.app.Activity
import android.os.Bundle
import androidx.multidex.MultiDexApplication

actual open class NativeApplication : MultiDexApplication() {

    var currentActivity: AppActivity? = null
        private set

    actual open fun startNativeServices() {}

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

            override fun onActivityResumed(p0: Activity) {}
            override fun onActivityPaused(p0: Activity) {}
            override fun onActivityStopped(p0: Activity) {}
            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
            override fun onActivityDestroyed(p0: Activity) {}
        })
    }

}