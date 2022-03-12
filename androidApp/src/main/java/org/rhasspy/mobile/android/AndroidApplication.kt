package org.rhasspy.mobile.android

import org.rhasspy.mobile.Application
import org.rhasspy.mobile.NativeApplication
import org.rhasspy.mobile.android.uiservices.WindowService

class AndroidApplication : Application() {

    init {
        Instance = this
    }

    companion object {
        lateinit var Instance: NativeApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        onCreated()
        WindowService.start()
    }


}