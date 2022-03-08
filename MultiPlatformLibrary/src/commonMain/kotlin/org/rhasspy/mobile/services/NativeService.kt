package org.rhasspy.mobile.services

import kotlin.native.concurrent.ThreadLocal

expect class NativeService() {

    @ThreadLocal
    companion object {

        var isRunning: Boolean

        fun doAction(action: Action)

        fun stop()

    }

}