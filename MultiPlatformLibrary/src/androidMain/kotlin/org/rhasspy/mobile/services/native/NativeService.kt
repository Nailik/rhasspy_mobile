package org.rhasspy.mobile.services.native

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.IBinder
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.services.Action
import org.rhasspy.mobile.services.ForegroundService
import org.rhasspy.mobile.services.ServiceNotification

/**
 * Native Service to run continuously in background
 */
actual class NativeService : android.app.Service() {

    /**
     * create service, show notification and start in foreground
     */
    override fun onCreate() {
        super.onCreate()

        //start service, display notification
        startForeground(ServiceNotification.ONGOING_NOTIFICATION_ID, ServiceNotification.create())
    }

    /**
     * do action according to params in intent, set is running to true
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true

        intent?.also { i ->
            i.getStringExtra(ACTION)?.also {
                ForegroundService.action(Action.valueOf(it), true)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * set running flag when service is destroyed
     */
    override fun onDestroy() {
        isRunning = false
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    actual companion object {

        const val ACTION = "Action"

        //stores if services is currently running
        actual var isRunning: Boolean = false

        /**
         * When there is an action to be done by the services
         */
        actual fun doAction(action: Action) {
            val intent = Intent(Application.Instance, NativeService::class.java).apply {
                putExtra(ACTION, action.name)
            }
            if (SDK_INT >= Build.VERSION_CODES.O) {
                Application.Instance.startForegroundService(intent)
            } else {
                Application.Instance.startService(intent)
            }
        }

        /**
         * stop background work
         */
        actual fun stop() {
            Application.Instance.stopService(Intent(Application.Instance, ForegroundService::class.java))
        }
    }

}