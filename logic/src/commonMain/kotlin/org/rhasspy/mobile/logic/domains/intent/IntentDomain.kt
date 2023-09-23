package org.rhasspy.mobile.logic.domains.intent

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.domain.IntentDomainData
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.*
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult.IntentNotRecognized
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult.IntentRecognitionResult
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.pipeline.IntentResult
import org.rhasspy.mobile.logic.pipeline.IntentResult.Intent
import org.rhasspy.mobile.logic.pipeline.IntentResult.NotRecognized
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.Transcript
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult as MqttIntentResult

interface IIntentDomain : IService {

    suspend fun awaitIntent(sessionId: String, transcript: Transcript): IntentResult

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class IntentDomain(
    private val params: IntentDomainData,
    private val mqttConnection: IMqttConnection,
    private val rhasspy2HermesConnection: IRhasspy2HermesConnection,
) : IIntentDomain {

    private val logger = Logger.withTag("IntentRecognitionService")

    override val serviceState = MutableStateFlow<ServiceState>(Pending)

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        serviceState.value = when (params.option) {
            IntentRecognitionOption.Rhasspy2HermesHttp -> Success
            IntentRecognitionOption.Rhasspy2HermesMQTT -> Success
            IntentRecognitionOption.Disabled           -> Disabled
        }
    }

    override suspend fun awaitIntent(sessionId: String, transcript: Transcript): IntentResult {
        return when (params.option) {
            IntentRecognitionOption.Rhasspy2HermesHttp ->
                awaitRhasspy2HermesHttpIntent(
                    transcript = transcript,
                )

            IntentRecognitionOption.Rhasspy2HermesMQTT ->
                awaitRhasspy2HermesMQTTIntent(
                    sessionId = sessionId,
                    transcript = transcript,
                )

            IntentRecognitionOption.Disabled           -> NotRecognized
        }
    }

    private suspend fun awaitRhasspy2HermesHttpIntent(transcript: Transcript): IntentResult {
        val result = rhasspy2HermesConnection.recognizeIntent(transcript.text)
        serviceState.value = result.toServiceState()

        return when (result) {
            is HttpClientResult.HttpClientError -> NotRecognized
            is HttpClientResult.Success         -> {
                val intentName = readIntentNameFromJson(result.data)
                Intent(intentName = intentName, intent = result.data)
            }
        }
    }

    private suspend fun awaitRhasspy2HermesMQTTIntent(sessionId: String, transcript: Transcript): IntentResult {
        mqttConnection.recognizeIntent(
            sessionId = sessionId,
            text = transcript.text,
            onResult = { serviceState.value = it }
        )

        return mqttConnection.incomingMessages
            .filterIsInstance<MqttIntentResult>()
            .map {
                when (it) {
                    is IntentRecognitionResult        -> Intent(it.intentName, it.intent)
                    is IntentNotRecognized -> NotRecognized
                }
            }
            .first()
            //TODO timeout
    }

    override fun stop() {
        scope.cancel()
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