package org.rhasspy.mobile.nativeutils

import kotlin.native.concurrent.ThreadLocal

/**
 * Native Service to run continuously in background
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class NativeServiceInterop() {

    @ThreadLocal
    companion object {

        //stores if services is currently running
        var isRunning: Boolean

        /**
         * When there is an action to be done by the services
         */
        fun doAction(/*serviceAction: ServiceAction*/)

        /**
         * stop background work
         */
        fun stop()

    }

}