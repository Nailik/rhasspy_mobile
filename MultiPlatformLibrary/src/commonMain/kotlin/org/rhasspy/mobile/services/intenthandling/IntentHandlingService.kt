package org.rhasspy.mobile.services.intenthandling

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.homeassistant.HomeAssistantService
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.settings.option.IntentHandlingOption

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
open class IntentHandlingService : IService() {
    private val logger = LogType.IntentHandlingService.logger()

    private val params by inject<IntentHandlingServiceParams>()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success())
    val serviceState = _serviceState.readOnly

    private val httpClientService by inject<HttpClientService>()
    private val homeAssistantService by inject<HomeAssistantService>()

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
            IntentHandlingOption.HomeAssistant -> homeAssistantService.sendIntent(intentName, intent)
            IntentHandlingOption.RemoteHTTP -> httpClientService.intentHandling(intent)
            IntentHandlingOption.WithRecognition -> {}
            IntentHandlingOption.Disabled -> {}
        }
    }

}