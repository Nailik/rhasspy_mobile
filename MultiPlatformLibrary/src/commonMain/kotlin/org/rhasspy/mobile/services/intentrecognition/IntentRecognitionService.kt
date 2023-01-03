package org.rhasspy.mobile.services.intentrecognition

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.middleware.Action.DialogAction
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.middleware.Source
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.httpclient.HttpClientResult
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.settings.option.IntentRecognitionOption

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
open class IntentRecognitionService : IService() {
    private val logger = LogType.IntentRecognitionService.logger()

    private val params by inject<IntentRecognitionServiceParams>()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success())
    val serviceState = _serviceState.readOnly

    private val httpClientService by inject<HttpClientService>()
    private val mqttClientService by inject<MqttService>()

    private val serviceMiddleware by inject<IServiceMiddleware>()

    /**
     * hermes/nlu/query
     * Request an intent to be recognized from text
     *
     * Response(s)
     * hermes/intent/<intentName>
     * hermes/nlu/intentNotRecognized
     *
     * HTTP:
     * - calls service to recognize intent from text
     * - if IntentHandlingOptions.WithRecognition is set the remote site will also automatically handle the intent
     * - later intentRecognized or intentNotRecognized will be called with received data
     *
     * MQTT:
     * - calls default site to recognize intent
     * - later eventually intentRecognized or intentNotRecognized will be called with received data
     */
    suspend fun recognizeIntent(sessionId: String, text: String) {
        logger.d { "recognizeIntent sessionId: $sessionId text: $text" }
        when (params.intentRecognitionOption) {
            IntentRecognitionOption.RemoteHTTP -> {
                val action = when (val result = httpClientService.recognizeIntent(text)) {
                    is HttpClientResult.Error -> DialogAction.IntentRecognitionError(Source.HttpApi)
                    is HttpClientResult.Success -> DialogAction.IntentRecognitionResult(Source.HttpApi, "", result.data)
                }
                serviceMiddleware.action(action)
            }
            IntentRecognitionOption.RemoteMQTT -> mqttClientService.recognizeIntent(sessionId, text)
            IntentRecognitionOption.Disabled -> {}
        }
    }

}