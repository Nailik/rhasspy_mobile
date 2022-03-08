package org.rhasspy.mobile.services

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.IBinder

actual class NativeService : android.app.Service() {

    override fun onCreate() {
        super.onCreate()

        //start service, display notification
        startForeground(ServiceNotification.ONGOING_NOTIFICATION_ID, ServiceNotification.create())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.also { i ->
            i.getStringExtra(ACTION)?.also {
                ForegroundService.action(Action.valueOf(it), true)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    actual companion object {

        const val ACTION = "Action"

        actual fun doAction(action: Action) {
            val intent = Intent(Application.Instance, ForegroundService::class.java).apply {
                putExtra(ACTION, action.name)
            }
            if (SDK_INT >= Build.VERSION_CODES.O) {
                Application.Instance.startForegroundService(intent)
            } else {
                Application.Instance.startService(intent)
            }
        }
    }

}