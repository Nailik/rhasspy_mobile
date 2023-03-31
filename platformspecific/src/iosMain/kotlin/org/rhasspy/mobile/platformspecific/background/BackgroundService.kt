package org.rhasspy.mobile.platformspecific.background

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