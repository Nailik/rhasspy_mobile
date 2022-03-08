package org.rhasspy.mobile.services

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

        ForegroundService.action(Action.Start)
    }

}