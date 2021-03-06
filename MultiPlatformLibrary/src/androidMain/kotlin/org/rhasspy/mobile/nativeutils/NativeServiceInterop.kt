package org.rhasspy.mobile.nativeutils

import android.content.Intent
import android.os.Build
import android.os.IBinder
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.handler.ForegroundServiceHandler
import org.rhasspy.mobile.services.ServiceAction

/**
 * Native Service to run continuously in background
 */
actual class NativeServiceInterop : android.app.Service() {
    private val logger = Logger.withTag("NativeService")
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

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
     * do action according to params in intent, set is running to true
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logger.d { "onStartCommand" }
        isRunning = true

        intent?.also { i ->
            i.getStringExtra(ACTION)?.also {
                coroutineScope.launch {
                    ForegroundServiceHandler.action(ServiceAction.valueOf(it), true)
                }
            } ?: run {
                logger.w { "no ACTION extra in intent" }
            }
        } ?: run {
            logger.w { "started without intent" }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * set running flag when service is destroyed
     */
    override fun onDestroy() {
        logger.d { "onDestroy" }
        isRunning = false
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    actual companion object {
        private val logger = Logger.withTag("NativeService")

        const val ACTION = "Action"

        //stores if services is currently running
        actual var isRunning: Boolean = false

        /**
         * When there is an action to be done by the services
         */
        actual fun doAction(serviceAction: ServiceAction) {
            logger.d { "doAction $serviceAction" }

            val intent = Intent(Application.Instance, NativeServiceInterop::class.java).apply {
                putExtra(ACTION, serviceAction.name)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Application.Instance.startForegroundService(intent)
            } else {
                Application.Instance.startService(intent)
            }
        }

        /**
         * stop background work
         */
        actual fun stop() {
            logger.d { "stop" }

            Application.Instance.stopService(Intent(Application.Instance, ForegroundServiceHandler::class.java))
        }
    }

}