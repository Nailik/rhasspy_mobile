package org.rhasspy.mobile.logic.domains.handle

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.domain.HandleDomainData
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.homeassistant.IHomeAssistantConnection
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.EndSession
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnection
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent.WebServerSay
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.pipeline.HandleResult
import org.rhasspy.mobile.logic.pipeline.HandleResult.Handle
import org.rhasspy.mobile.logic.pipeline.HandleResult.NotHandled
import org.rhasspy.mobile.logic.pipeline.IntentResult
import org.rhasspy.mobile.logic.pipeline.IntentResult.Intent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.HandleDomainEvent.HandledEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.HandleDomainEvent.NotHandledEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.IntentDomainEvent.IntentEvent

interface IHandleDomain : IService {

    suspend fun awaitIntentHandle(sessionId: String, intent: Intent) : HandleResult

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class HandleDomain(
    private val params: HandleDomainData,
    private val mqttConnection: IMqttConnection,
    private val homeAssistantConnection: IHomeAssistantConnection,
    private val webServerConnection: IWebServerConnection,
    private val indication: IIndication,
) : IHandleDomain {

    private val logger = Logger.withTag("IntentHandlingService")

    override val serviceState = MutableStateFlow<ServiceState>(Pending)

    init {
        serviceState.value = when (params.option) {
            IntentHandlingOption.HomeAssistant      -> Success
            IntentHandlingOption.Disabled           -> Disabled
        }
    }

    override suspend fun awaitIntentHandle(sessionId: String, intent: Intent) : HandleResult {
        indication.onThinking()

        return when (params.option) {
            IntentHandlingOption.HomeAssistant      -> awaitHomeAssistantHandle(sessionId, intent)
            IntentHandlingOption.Disabled           -> NotHandled
        }
    }

    private suspend fun awaitHomeAssistantHandle(sessionId: String, intent: Intent) : HandleResult {
        return when (params.homeAssistantIntentHandlingOption) {
            HomeAssistantIntentHandlingOption.Event  -> awaitHomeAssistantEventHandle(sessionId, intent)
            HomeAssistantIntentHandlingOption.Intent -> awaitHomeAssistantIntentHandle(intent)
        }
    }

    private suspend fun awaitHomeAssistantIntentHandle(intent: Intent) : HandleResult {
        return when (val result = homeAssistantConnection.awaitIntent(intent.intentName, intent.intent)) {
            is HttpClientResult.HttpClientError -> NotHandled
            is HttpClientResult.Success         -> Handle(result.data ?: return NotHandled)
        }
    }

    private suspend fun awaitHomeAssistantEventHandle(sessionId: String, intent: Intent) : HandleResult {
        homeAssistantConnection.awaitEvent(intent.intentName, intent.intent)

        //TODO timeout
        //await for EndSession or Say
        return merge(
            mqttConnection.incomingMessages
                .filterIsInstance<EndSession>()
                .filter { it.sessionId == sessionId }
                .map {
                    Handle(it.text)
                },
            webServerConnection.incomingMessages
                .filterIsInstance<WebServerSay>()
                .map {
                    Handle(it.text)
                },
        ).first()
    }

    override fun stop() { }

}