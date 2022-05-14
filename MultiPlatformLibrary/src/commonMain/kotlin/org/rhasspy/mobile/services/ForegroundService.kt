package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.services.native.NativeService
import org.rhasspy.mobile.settings.AppSettings

/**
 * Start point of all services
 *
 * handles
 * - WakeWord Service
 * - Listening Service
 * - MQTT Services
 * - HTTP Services
 */
object ForegroundService {
    private val logger = Logger.withTag("ForegroundService")

    init {
        //when background enabled value changes, services need to be reloaded
        AppSettings.isBackgroundEnabled.value.addObserver {
            CoroutineScope(Dispatchers.Default).launch {
                action(ServiceAction.Reload)
            }
        }
    }

    /**
     * Action to Start, Stop o Reload service
     *
     * Starts background service, if not called by service and
     * isBackgroundEnabled is true and service is not running yet
     */
    suspend fun action(serviceAction: ServiceAction, fromService: Boolean = false) {
        logger.v { "action $serviceAction fromService $fromService" }

        if (fromService) {
            ServiceInterface.serviceAction(serviceAction)
        } else {
            if (!AppSettings.isBackgroundEnabled.data) {
                if (NativeService.isRunning) {
                    //should not be running, stop it
                    NativeService.stop()
                }

                ServiceInterface.serviceAction(serviceAction)
            } else {
                //start or update service
                NativeService.doAction(serviceAction)
            }
        }
    }
}