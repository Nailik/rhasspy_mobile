package org.rhasspy.mobile.platformspecific.background

/**
 * Native Service to run continuously in background
 */
expect class BackgroundService() {

    /**
     * start background service
     */
    fun start()

    /**
     * stop background work
     */
    fun stop()

}