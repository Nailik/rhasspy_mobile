package org.rhasspy.mobile.platformspecific.background

import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual class BackgroundService actual constructor(
    private val nativeApplication: NativeApplication
) {

    /**
     * start background service
     */
    actual fun start() {
        //TODO("Not yet implemented")
    }

    /**
     * stop background work
     */
    actual fun stop() {
        //TODO("Not yet implemented")
    }

}