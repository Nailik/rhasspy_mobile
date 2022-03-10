package org.rhasspy.mobile

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.services.ForegroundService

abstract class Application : NativeApplication() {

    companion object {
        lateinit var Instance: NativeApplication
            private set
    }

    init {
        @Suppress("LeakingThis")
        Instance = this
    }

    fun onCreated() {
        Logger.addLogWriter(FileLogger)
        ForegroundService
    }

}