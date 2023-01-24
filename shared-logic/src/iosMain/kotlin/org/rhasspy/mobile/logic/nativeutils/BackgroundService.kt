package org.rhasspy.mobile.logic.nativeutils

actual class BackgroundService {

    @ThreadLocal
    actual companion object {

        /**
         * start background service
         */
        actual fun start() {
        }

        /**
         * stop background work
         */
        actual fun stop() {
        }

    }
}