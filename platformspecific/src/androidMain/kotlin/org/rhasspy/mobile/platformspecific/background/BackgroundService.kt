package org.rhasspy.mobile.platformspecific.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import co.touchlab.kermit.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.NativeApplication

/**
 * Native Service to run continuously in background
 */
internal actual class BackgroundService : IBackgroundService, Service(), KoinComponent {
    private val nativeApplication = get<NativeApplication>()

    private val logger = Logger.withTag("BackgroundService")
    private val intent = Intent(nativeApplication, BackgroundService::class.java)

    /**
     * start background service
     */
    actual override fun start() {
        logger.d { "start" }
        //start normally because the notification needs to be shown within 5 second but on create may be called later
        nativeApplication.startService(intent)
    }

    /**
     * stop background work
     */
    actual override fun stop() {
        logger.d { "stop" }
        nativeApplication.stopService(intent)
    }


    /**
     * create service, show notification and start in foreground
     */
    override fun onCreate() {
        super.onCreate()
        logger.d { "onCreate" }
        //start as foreground service and instantly display notification
        ContextCompat.startForegroundService(this, intent)
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