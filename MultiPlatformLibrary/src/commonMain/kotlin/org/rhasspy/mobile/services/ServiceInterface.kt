package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.handler.ForegroundServiceHandler
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.server.HttpServer
import org.rhasspy.mobile.viewModels.GlobalData

/**
 * to start and stop services that require it
 */
object ServiceInterface {

    private val logger = Logger.withTag("ServerService")
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    //call all services to activate them
    init {
        HotWordService
        IndicationService
        RecordingService
    }

    /**
     * Start services according to settings
     */
    suspend fun serviceAction(serviceAction: ServiceAction) {
        logger.d { "serviceAction ${serviceAction.name}" }

        when (serviceAction) {
            ServiceAction.Start -> {
                UdpService.start()
                HttpServer.start()
                MqttService.start()
                StateMachine.started()
            }
            ServiceAction.Stop -> {
                StateMachine.stopped()
                UdpService.stop()
                HttpServer.stop()
                MqttService.stop()
            }
            ServiceAction.Reload -> {
                serviceAction(ServiceAction.Stop)
                serviceAction(ServiceAction.Start)
            }
        }
    }

    //foreground service handler starts itself
    /**
     * Saves configuration changes
     */
    fun saveAndApplyChanges() {
        coroutineScope.launch {
            GlobalData.saveAllChanges()

            ForegroundServiceHandler.action(ServiceAction.Reload)
        }
    }

    /**
     * resets configuration changes
     */
    fun resetChanges() {
        GlobalData.resetChanges()
    }

}