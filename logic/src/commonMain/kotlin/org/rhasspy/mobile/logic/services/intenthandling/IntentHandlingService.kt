package org.rhasspy.mobile.logic.services.intenthandling

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.Disabled
import org.rhasspy.mobile.data.service.ServiceState.Pending
import org.rhasspy.mobile.data.service.ServiceState.Success
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.homeassistant.IHomeAssistantService
import org.rhasspy.mobile.logic.services.httpclient.IHttpClientService
import org.rhasspy.mobile.platformspecific.readOnly

interface IIntentHandlingService : IService {

    override val serviceState: StateFlow<ServiceState>

    fun intentHandling(intentName: String, intent: String)

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class IntentHandlingService(
    paramsCreator: IntentHandlingServiceParamsCreator,
) : IIntentHandlingService {

    override val logger = LogType.IntentHandlingService.logger()

    private val httpClientService by inject<IHttpClientService>()
    private val homeAssistantService by inject<IHomeAssistantService>()

    private val _serviceState = MutableStateFlow<ServiceState>(Pending)
    override val serviceState = _serviceState.readOnly

    private val paramsFlow: StateFlow<IntentHandlingServiceParams> = paramsCreator()
    private val params get() = paramsFlow.value

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            paramsFlow.collect {
                updateState()
            }
        }
    }

    private fun updateState() {
        _serviceState.value = when (params.intentHandlingOption) {
            IntentHandlingOption.HomeAssistant -> Success
            IntentHandlingOption.RemoteHTTP -> Success
            IntentHandlingOption.WithRecognition -> Success
            IntentHandlingOption.Disabled -> Disabled
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
    override fun intentHandling(intentName: String, intent: String) {
        logger.d { "intentHandling intentName: $intentName intent: $intent" }
        when (params.intentHandlingOption) {
            IntentHandlingOption.HomeAssistant ->
                homeAssistantService.sendIntent(intentName, intent) {
                    _serviceState.value = it
                }

            IntentHandlingOption.RemoteHTTP ->
                httpClientService.intentHandling(intent) {
                    _serviceState.value = it.toServiceState()
                }

            IntentHandlingOption.WithRecognition -> Unit
            IntentHandlingOption.Disabled -> Unit
        }
    }

}