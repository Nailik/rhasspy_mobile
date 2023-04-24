package org.rhasspy.mobile.logic.services.intenthandling

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.homeassistant.HomeAssistantService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.platformspecific.readOnly

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
open class IntentHandlingService(
    paramsCreator: IntentHandlingServiceParamsCreator,
) : IService(LogType.IntentHandlingService) {

    private val httpClientService by inject<HttpClientService>()
    private val homeAssistantService by inject<HomeAssistantService>()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success)
    override val serviceState = _serviceState.readOnly

    private val paramsFlow: StateFlow<IntentHandlingServiceParams> = paramsCreator()
    private val params get() = paramsFlow.value

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
    suspend fun intentHandling(intentName: String, intent: String) {
        logger.d { "intentHandling intentName: $intentName intent: $intent" }
        when (params.intentHandlingOption) {
            IntentHandlingOption.HomeAssistant -> _serviceState.value =
                homeAssistantService.sendIntent(intentName, intent)

            IntentHandlingOption.RemoteHTTP -> _serviceState.value =
                httpClientService.intentHandling(intent).toServiceState()

            IntentHandlingOption.WithRecognition -> {}
            IntentHandlingOption.Disabled -> {}
        }
    }

}