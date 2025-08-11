package org.rhasspy.mobile.platformspecific.background

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nativeApplication.startForegroundService(intent)
        } else {
            nativeApplication.startService(intent)
        }
        CoroutineScope(Dispatchers.IO).launch {
            //await for app to be in foreground bc service is not allowed to start in background
            nativeApplication.isAppInBackground.first { !it }
            //start as foreground service

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logger.d { "onStartCommand" }
        startAsForegroundService()
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * stop background work
     */
    actual override fun stop() {
        logger.d { "stop" }
        nativeApplication.stopService(intent)
    }

    /**
     * foreground service is stopped
     */
    override fun onDestroy() {
        logger.d { "onDestroy" }
        super.onDestroy()
    }

    /**
     * Promotes the service to a foreground service, showing a notification to the user.
     *
     * This needs to be called within 10 seconds of starting the service or the system will throw an exception.
     */
    private fun startAsForegroundService() {
        // promote service to foreground service
        ServiceCompat.startForeground(
            /* service = */ this,
            /* id = */ ServiceNotification.ONGOING_NOTIFICATION_ID,
            /* notification = */ ServiceNotification.create(),
            /* foregroundServiceType = */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            } else 0
        )
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}