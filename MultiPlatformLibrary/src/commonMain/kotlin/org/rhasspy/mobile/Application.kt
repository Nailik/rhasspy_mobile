package org.rhasspy.mobile

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.services.ForegroundService

abstract class Application : NativeApplication() {
    private val logger = Logger.withTag(Application::class.simpleName!!)

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

        logger.i { "logger created" }

        ForegroundService
    }

}