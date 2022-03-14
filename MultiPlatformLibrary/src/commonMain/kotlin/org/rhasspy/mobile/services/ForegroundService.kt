package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.services.http.HttpServer
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

        ServiceInterface.isListenForWakeEnabled.addObserver {
            if (it) {
                startWakeWordService()
            } else {
                NativeLocalWakeWordService.stop()
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
            if (!AppSettings.isBackgroundEnabled.data) {
                if (NativeService.isRunning) {
                    //should not be running, stop it
                    NativeService.stop()
                }

                when (action) {
                    Action.Start -> startServices()
                    Action.Stop -> stopServices()
                    Action.Reload -> reloadServices()
                }
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

        startWakeWordService()
        HttpServer.start()
        MqttService.start()
    }

    /**
     * Stop services according to settings
     */
    private fun stopServices() {
        logger.d { "stopServices" }

        NativeLocalWakeWordService.stop()
        HttpServer.stop()
        MqttService.stop()
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

    private fun startWakeWordService() {
        if (ConfigurationSettings.wakeWordOption.data == WakeWordOption.Porcupine &&
            ConfigurationSettings.wakeWordAccessToken.data.isNotEmpty()
        ) {
            NativeLocalWakeWordService.start()
        }
    }
}