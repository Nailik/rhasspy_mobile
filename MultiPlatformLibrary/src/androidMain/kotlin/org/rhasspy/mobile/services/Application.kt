package org.rhasspy.mobile.services

import android.content.Intent
import android.os.Build
import androidx.multidex.MultiDexApplication

class Application : MultiDexApplication() {
    companion object {
        lateinit var Instance: Application
            private set
    }

    init {
        Instance = this
    }

    override fun onCreate() {
        super.onCreate()

        startServices()
    }

}

fun startServices() {
    val intent = Intent(Application.Instance, ForegroundService::class.java) // Build the intent for the service
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Application.Instance.startForegroundService(intent)
    } else {
        Application.Instance.startService(intent)
    }
}