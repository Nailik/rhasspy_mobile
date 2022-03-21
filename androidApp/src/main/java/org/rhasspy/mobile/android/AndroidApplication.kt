package org.rhasspy.mobile.android

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.NativeApplication
import org.rhasspy.mobile.android.uiservices.IndicationOverlay
import org.rhasspy.mobile.android.uiservices.MicrophoneOverlay

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
        //catches all exceptions
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            Logger.withTag("AndroidApplication").e(exception) {
                "uncaught exception in Thread $thread"
            }
        }
    }

    override fun startNativeServices() {
        IndicationOverlay.start()
        MicrophoneOverlay.start()
    }

}