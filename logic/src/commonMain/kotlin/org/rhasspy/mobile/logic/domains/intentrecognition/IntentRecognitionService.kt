package org.rhasspy.mobile.logic.domains.intentrecognition

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.component.inject
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.IntentRecognitionError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.IntentRecognitionResult
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.platformspecific.readOnly

interface IIntentRecognitionService : IService {

    override val serviceState: StateFlow<ServiceState>

    fun recognizeIntent(sessionId: String, text: String)

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class IntentRecognitionService(
    paramsCreator: IntentRecognitionServiceParamsCreator
) : IIntentRecognitionService {

    private val logger = LogType.IntentRecognitionService.logger()

    private val serviceMiddleware by inject<IServiceMiddleware>()
    private val mqttClientService by inject<IMqttConnection>()
    private val httpClientConnection by inject<IRhasspy2HermesConnection>()

    private val paramsFlow: StateFlow<IntentRecognitionServiceParams> = paramsCreator()
    private val params get() = paramsFlow.value

    private val _serviceState = MutableStateFlow<ServiceState>(Pending)
    override val serviceState = _serviceState.readOnly

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            paramsFlow.collect {
                setupState()
            }
        }
    }

    private fun setupState() {
        _serviceState.value = when (params.intentRecognitionOption) {
            IntentRecognitionOption.Rhasspy2HermesHttp -> Success
            IntentRecognitionOption.Rhasspy2HermesMQTT -> Success
            IntentRecognitionOption.Disabled           -> Disabled
        }
    }

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
    override fun recognizeIntent(sessionId: String, text: String) {
        logger.d { "recognizeIntent sessionId: $sessionId text: $text" }
        when (params.intentRecognitionOption) {
            IntentRecognitionOption.Rhasspy2HermesHttp -> {
                httpClientConnection.recognizeIntent(text) { result ->
                    _serviceState.value = result.toServiceState()
                    val action = when (result) {
                        is HttpClientResult.Error      -> IntentRecognitionError(Source.Local)
                        is HttpClientResult.Success    -> IntentRecognitionResult(
                            source = Source.Local,
                            intentName = readIntentNameFromJson(result.data),
                            intent = result.data
                        )

                        is HttpClientResult.KnownError -> IntentRecognitionError(Source.Local)
                    }
                    serviceMiddleware.action(action)
                }
            }

            IntentRecognitionOption.Rhasspy2HermesMQTT -> mqttClientService.recognizeIntent(sessionId, text) {
                _serviceState.value = it
            }

            IntentRecognitionOption.Disabled -> serviceMiddleware.action(
                IntentRecognitionResult(
                    Source.Local,
                    "",
                    ""
                )
            )
        }
    }

    /**
     * read the intent name from json
     */
    private fun readIntentNameFromJson(intent: String): String {
        return try {
            Json.decodeFromString<JsonObject>(intent).jsonObject["intent"]?.jsonObject?.get("name")?.jsonPrimitive?.content
                ?: ""
        } catch (e: Exception) {
            ""
        }
    }

}