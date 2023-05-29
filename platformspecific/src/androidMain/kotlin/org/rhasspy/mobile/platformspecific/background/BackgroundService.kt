package org.rhasspy.mobile.platformspecific.background

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import co.touchlab.kermit.Logger
import org.rhasspy.mobile.platformspecific.application.NativeApplication

/**
 * Native Service to run continuously in background
 */
actual class BackgroundService actual constructor(
    private val nativeApplication: NativeApplication
) : Service() {

    private val logger = Logger.withTag("BackgroundService")
    private val intent = Intent(nativeApplication, BackgroundService::class.java)

    /**
     * start background service
     */
    actual fun start() {
        logger.d { "start" }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nativeApplication.startForegroundService(intent)
        } else {
            nativeApplication.startService(intent)
        }
    }

    /**
     * stop background work
     */
    actual fun stop() {
        logger.d { "stop" }
        nativeApplication.stopService(intent)
    }


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

}