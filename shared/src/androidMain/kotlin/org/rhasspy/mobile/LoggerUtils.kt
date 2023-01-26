package org.rhasspy.mobile

import co.touchlab.kermit.Logger
import kotlin.system.exitProcess

actual fun Logger.unhandledExceptionHook() {
    //catches all exceptions
    Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
        println(exception)
        this.a(exception) { "uncaught exception in Thread $thread" }
        exitProcess(2)
    }
}