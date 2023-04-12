package org.rhasspy.mobile.logic.services.intentrecognition

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.component.inject
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.middleware.Action.DialogAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientResult
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
open class IntentRecognitionService : IService(LogType.IntentRecognitionService) {
    private val params by inject<IntentRecognitionServiceParams>()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success)
    override val serviceState = _serviceState.readOnly

    private val httpClientService by inject<HttpClientService>()
    private val mqttClientService by inject<MqttService>()

    private val serviceMiddleware by inject<ServiceMiddleware>()

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
                val result = httpClientService.recognizeIntent(text)
                _serviceState.value = result.toServiceState()
                val action = when (result) {
                    is HttpClientResult.Error -> DialogAction.IntentRecognitionError(Source.HttpApi)
                    is HttpClientResult.Success -> DialogAction.IntentRecognitionResult(
                        Source.HttpApi,
                        readIntentNameFromJson(result.data),
                        result.data
                    )
                }
                serviceMiddleware.action(action)
            }

            IntentRecognitionOption.RemoteMQTT -> _serviceState.value = mqttClientService.recognizeIntent(sessionId, text)
            IntentRecognitionOption.Disabled -> serviceMiddleware.action(DialogAction.IntentRecognitionResult(Source.Local, "",""))
        }
    }

    /**
     * read the intent name from json
     */
    private fun readIntentNameFromJson(intent: String): String {
        return try {
            Json.decodeFromString<JsonObject>(intent).jsonObject["intent"]?.jsonObject?.get("name")?.jsonPrimitive?.content ?: ""
        } catch (e: Exception) {
            ""
        }
    }

}