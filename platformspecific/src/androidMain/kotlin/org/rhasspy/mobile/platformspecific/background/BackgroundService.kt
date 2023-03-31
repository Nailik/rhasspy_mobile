package org.rhasspy.mobile.platformspecific.background

import android.content.Intent
import android.os.Build
import android.os.IBinder
import co.touchlab.kermit.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.NativeApplication

/**
 * Native Service to run continuously in background
 */
actual class BackgroundService : android.app.Service() {

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

    actual companion object : KoinComponent {
        private val logger = Logger.withTag("BackgroundService")
        private val context = get<NativeApplication>()
        private val intent = Intent(context, BackgroundService::class.java)

        /**
         * start background service
         */
        actual fun start() {
            logger.d { "start" }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        /**
         * stop background work
         */
        actual fun stop() {
            logger.d { "stop" }
            context.stopService(intent)
        }
    }

}