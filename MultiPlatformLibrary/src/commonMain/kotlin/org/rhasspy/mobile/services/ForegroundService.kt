package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.services.dialogue.ServiceInterface
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
    private val logger = Logger.withTag(this::class.simpleName!!)

    init {
        //when background enabled value changes, services need to be reloaded
        AppSettings.isBackgroundEnabled.value.addObserver {
            CoroutineScope(Dispatchers.Default).launch {
                action(Action.Reload)
            }
        }
    }

    /**
     * Action to Start, Stop o Reload service
     *
     * Starts background service, if not called by service and
     * isBackgroundEnabled is true and service is not running yet
     */
    fun action(action: Action, fromService: Boolean = false) {
        logger.v { "action $action fromService $fromService" }

        if (fromService) {
            when (action) {
                Action.Start -> ServiceInterface.startServices()
                Action.Stop -> ServiceInterface.stopServices()
                Action.Reload -> ServiceInterface.reloadServices()
            }
        } else {
            if (!AppSettings.isBackgroundEnabled.data) {
                if (NativeService.isRunning) {
                    //should not be running, stop it
                    NativeService.stop()
                }

                when (action) {
                    Action.Start -> ServiceInterface.startServices()
                    Action.Stop -> ServiceInterface.stopServices()
                    Action.Reload -> ServiceInterface.reloadServices()
                }
            } else {
                //start or update service
                NativeService.doAction(action)
            }
        }
    }
}