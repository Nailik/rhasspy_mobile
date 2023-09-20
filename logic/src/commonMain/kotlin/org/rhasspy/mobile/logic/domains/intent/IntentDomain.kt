package org.rhasspy.mobile.logic.domains.intent

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.pipeline.IPipeline
import org.rhasspy.mobile.logic.pipeline.PipelineEvent.IntentDomainEvent.*
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IIntentDomain : IService {

    fun onRecognize(recognizeEvent: RecognizeEvent, sessionId: String)

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class IntentDomain(
    private val pipeline: IPipeline,
    private val mqttClientConnection: IMqttConnection,
    private val httpClientConnection: IRhasspy2HermesConnection,
) : IIntentDomain {

    private val logger = Logger.withTag("IntentRecognitionService")

    override val serviceState = MutableStateFlow<ServiceState>(Pending)

    private val params get() = ConfigurationSetting.intentDomainData.value

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            ConfigurationSetting.intentDomainData.data.collectLatest {
                initialize()
            }
        }
    }

    private fun initialize() {
        serviceState.value = when (params.option) {
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
    override fun onRecognize(recognizeEvent: RecognizeEvent, sessionId: String) {
        logger.d { "recognizeIntent sessionId: $recognizeEvent" }
        serviceState.value = when (params.option) {
            IntentRecognitionOption.Rhasspy2HermesHttp -> {
                httpClientConnection.recognizeIntent(recognizeEvent.text) { result ->

                    serviceState.value = result.toServiceState()

                    val event = when (result) {
                        is HttpClientResult.HttpClientError -> NotRecognizedEvent(result.toString())
                        is HttpClientResult.Success         -> IntentEvent(
                            name = readIntentNameFromJson(result.data),
                            entities = result.data
                        )
                    }

                    pipeline.onEvent(event)
                }
                Loading
            }

            IntentRecognitionOption.Rhasspy2HermesMQTT -> {
                mqttClientConnection.recognizeIntent(
                    sessionId = sessionId,
                    text = recognizeEvent.text,
                    onResult = { serviceState.value = it }
                )
                Loading
            }

            IntentRecognitionOption.Disabled           -> Disabled
        }
    }

    /**
     * read the intent name from json
     */
    private fun readIntentNameFromJson(intent: String): String? {
        return try {
            Json.decodeFromString<JsonObject>(intent).jsonObject["intent"]?.jsonObject?.get("name")?.jsonPrimitive?.content
        } catch (e: Exception) {
            logger.e(e) { "unable to read intent name from json" }
            null
        }
    }

}