package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.services.native.NativeLocalWakeWordService
import org.rhasspy.mobile.services.native.NativeService
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings

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
        if (fromService) {
            when (action) {
                Action.Start -> startServices()
                Action.Stop -> stopServices()
                Action.Reload -> reloadServices()
            }
        } else {
            if (!AppSettings.isBackgroundEnabled.data && !NativeService.isRunning) {
                //stop service
                NativeService.stop()
            } else {
                //start or update service
                NativeService.doAction(action)
            }
        }
    }

    /**
     * Start services according to settings
     */
    private fun startServices() {
        logger.d { "startServices" }

        if (ConfigurationSettings.wakeWordOption.data == WakeWordOption.Porcupine &&
            ConfigurationSettings.wakeWordAccessToken.data.isNotEmpty()
        ) {
            NativeLocalWakeWordService.start()
        }
    }

    /**
     * Stop services according to settings
     */
    private fun stopServices() {
        logger.d { "stopServices" }

        NativeLocalWakeWordService.stop()
    }

    /**
     * Reload services according to settings
     * via start and stop
     */
    private fun reloadServices() {
        logger.d { "reloadServices" }

        stopServices()
        startServices()
    }
}