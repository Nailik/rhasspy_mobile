package org.rhasspy.mobile.logic.domains.handle

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.homeassistant.IHomeAssistantConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.pipeline.IPipeline
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.HandleDomainEvent.HandledEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.HandleDomainEvent.NotHandledEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.IntentDomainEvent.IntentEvent
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IHandleDomain : IService {

    fun onIntentEvent(event: IntentEvent)

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class HandleDomain(
    private val pipeline: IPipeline,
    private val homeAssistantConnection: IHomeAssistantConnection,
    private val httpClientConnection: IRhasspy2HermesConnection,
) : IHandleDomain {

    private val logger = Logger.withTag("IntentHandlingService")

    override val serviceState = MutableStateFlow<ServiceState>(Pending)

    private val params get() = ConfigurationSetting.handleDomainData.value

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            ConfigurationSetting.handleDomainData.data.collectLatest {
                initialize()
            }
        }
    }

    private fun initialize() {
        serviceState.value = when (params.option) {
            IntentHandlingOption.HomeAssistant      -> Success
            IntentHandlingOption.WithRecognition    -> Success
            IntentHandlingOption.Rhasspy2HermesHttp -> Success
            IntentHandlingOption.Disabled           -> Disabled
        }
    }

    /**
     * Only does something if intent handling is enabled
     *
     * HomeAssistant:
     * - calls Home Assistant Service
     *
     * HTTP:
     * - calls service to handle intent
     *
     * WithRecognition
     * - should only be used with HTTP text to intent
     * - remote text to intent will also handle it
     *
     * if local dialogue management it will end the session
     */
    override fun onIntentEvent(event: IntentEvent) {
        logger.d { "intentHandling intentName: $event" }
        serviceState.value = when (params.option) {
            IntentHandlingOption.HomeAssistant -> {
                homeAssistantConnection.sendIntent(
                    option = params.homeAssistantIntentHandlingOption,
                    intentName = event.name ?: "",
                    intent = event.entities,
                    onResult = {
                        val result = it.toServiceState()

                        pipeline.onEvent(
                            when (result) {
                                is Success -> HandledEvent(result.toString())
                                else       -> NotHandledEvent(result.toString())
                            }
                        )

                        serviceState.value = result
                    }
                )
                Loading
            }

            IntentHandlingOption.Rhasspy2HermesHttp -> {
                httpClientConnection.intentHandling(
                    intent = event.name ?: event.entities,
                    onResult = {
                        val result = it.toServiceState()

                        pipeline.onEvent(
                            when (result) {
                                is Success -> HandledEvent(result.toString())
                                else       -> NotHandledEvent(result.toString())
                            }
                        )

                        pipeline.onEvent(event)

                        serviceState.value = result
                    }
                )
                Loading
            }

            IntentHandlingOption.WithRecognition -> Success
            IntentHandlingOption.Disabled -> {
                pipeline.onEvent(NotHandledEvent("Disabled"))
                Disabled
            }
        }
    }

}