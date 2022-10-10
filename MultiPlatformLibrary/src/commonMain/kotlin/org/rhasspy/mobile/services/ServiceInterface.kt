package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.handler.ForegroundServiceHandler
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.server.HttpServer
import org.rhasspy.mobile.serviceInterfaces.HttpClientInterface
import kotlin.native.concurrent.ThreadLocal

/**
 * to start and stop services that require it
 */
@ThreadLocal
object ServiceInterface {

    private val logger = Logger.withTag("ServerService")
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var state = MutableStateFlow(ServiceState.Stopped)
    val currentState: StateFlow<ServiceState> get() = state

    //call all services to activate them
    init {
        HotWordService
        IndicationService
        RecordingService
        ForegroundServiceHandler
    }

    /**
     * Start services according to settings
     */
    suspend fun serviceAction(serviceAction: ServiceAction) {
        logger.d { "serviceAction ${serviceAction.name}" }

        when (serviceAction) {
            ServiceAction.Start -> {
                state.value = ServiceState.Starting
                HttpClientInterface.reloadHttpClient()
                UdpService.start()
                HttpServer.start()
                MqttService.start()
                StateMachine.started()
                state.value = ServiceState.Running
            }
            ServiceAction.Stop -> {
                state.value = ServiceState.Stopping
                StateMachine.stopped()
                UdpService.stop()
                HttpServer.stop()
                MqttService.stop()
                state.value = ServiceState.Stopped
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
            serviceAction(ServiceAction.Stop)


            ForegroundServiceHandler.action(ServiceAction.Reload)
        }
    }

    /**
     * resets configuration changes
     */
    fun resetChanges() {
    }

}