package org.rhasspy.mobile.android

import androidx.multidex.MultiDexApplication
import org.rhasspy.mobile.android.service.WindowService
import org.rhasspy.mobile.services.Action
import org.rhasspy.mobile.services.Application
import org.rhasspy.mobile.services.ForegroundService

class AndroidApplication : MultiDexApplication() {

    init {
        Application.Instance = this
    }

    override fun onCreate() {
        super.onCreate()

        ForegroundService.action(Action.Start)
        WindowService.start()
    }

}