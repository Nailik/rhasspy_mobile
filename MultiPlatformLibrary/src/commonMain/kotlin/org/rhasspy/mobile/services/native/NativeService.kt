package org.rhasspy.mobile.services.native

import org.rhasspy.mobile.services.Action
import kotlin.native.concurrent.ThreadLocal

expect class NativeService() {

    @ThreadLocal
    companion object {

        var isRunning: Boolean

        fun doAction(action: Action)

        fun stop()

    }

}