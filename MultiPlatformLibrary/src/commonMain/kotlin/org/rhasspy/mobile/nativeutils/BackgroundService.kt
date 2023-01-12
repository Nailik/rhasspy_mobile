package org.rhasspy.mobile.nativeutils

import kotlin.native.concurrent.ThreadLocal

/**
 * Native Service to run continuously in background
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class BackgroundService() {

    @ThreadLocal
    companion object {

        /**
         * start background service
         */
        fun start()

        /**
         * stop background work
         */
        fun stop()

    }

}