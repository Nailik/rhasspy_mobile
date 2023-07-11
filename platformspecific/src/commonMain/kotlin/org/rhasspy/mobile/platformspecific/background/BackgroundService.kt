package org.rhasspy.mobile.platformspecific.background

interface IBackgroundService {

    fun start()
    fun stop()

}

/**
 * Native Service to run continuously in background
 */
internal expect class BackgroundService() : IBackgroundService {

    /**
     * start background service
     */
    override fun start()

    /**
     * stop background work
     */
    override fun stop()

}