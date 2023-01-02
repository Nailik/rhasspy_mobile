package org.rhasspy.mobile.nativeutils

import android.content.Intent
import android.os.IBinder
import co.touchlab.kermit.Logger
import org.rhasspy.mobile.Application

/**
 * Native Service to run continuously in background
 */
actual class BackgroundService : android.app.Service() {
    private val logger = Logger.withTag("NativeService")

    /**
     * create service, show notification and start in foreground
     */
    override fun onCreate() {
        super.onCreate()
        logger.d { "onCreate" }
        //start service, display notification
        startForeground(ServiceNotification.ONGOING_NOTIFICATION_ID, ServiceNotification.create())
    }

    /**
     * foreground service is stopped
     */
    override fun onDestroy() {
        logger.d { "onDestroy" }
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    actual companion object {
        private val logger = Logger.withTag("NativeService")

        /**
         * start background service
         */
        actual fun start() {
            logger.d { "start" }
            Application.nativeInstance.startService(Intent(Application.nativeInstance, BackgroundService::class.java))
        }

        /**
         * stop background work
         */
        actual fun stop() {
            logger.d { "stop" }
            Application.nativeInstance.stopService(Intent(Application.nativeInstance, BackgroundService::class.java))
        }
    }

}