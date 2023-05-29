package org.rhasspy.mobile.platformspecific.background

import org.rhasspy.mobile.platformspecific.application.NativeApplication

/**
 * Native Service to run continuously in background
 */
expect class BackgroundService(
    nativeApplication: NativeApplication
) {

    /**
     * start background service
     */
    fun start()

    /**
     * stop background work
     */
    fun stop()

}