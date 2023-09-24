package org.rhasspy.mobile.logic.domains.intent

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
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
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.EndSession
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult.IntentNotRecognized
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult.IntentRecognitionResult
import org.rhasspy.mobile.logic.connections.mqtt.MqttResult
import org.rhasspy.mobile.logic.connections.mqtt.MqttResult.Error
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent.WebServerSay
import org.rhasspy.mobile.logic.pipeline.HandleResult
import org.rhasspy.mobile.logic.pipeline.HandleResult.Handle
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
    private val webServerConnection: IWebServerConnection,
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
                    sessionId = sessionId,
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

    private suspend fun awaitRhasspy2HermesHttpIntent(sessionId: String, transcript: Transcript): IntentResult {
        val result = rhasspy2HermesConnection.recognizeIntent(transcript.text)
        serviceState.value = result.toServiceState()

        //isRhasspy2HermesHttpHandleWithRecognition
        return when (result) {
            is HttpClientResult.HttpClientError -> NotRecognized
            is HttpClientResult.Success         -> {
                val intentName = readIntentNameFromJson(result.data)

                if (!params.isRhasspy2HermesHttpHandleWithRecognition) return Intent(intentName = intentName, intent = result.data)

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
        }
    }

    private suspend fun awaitRhasspy2HermesMQTTIntent(sessionId: String, transcript: Transcript): IntentResult {
        val result = mqttConnection.recognizeIntent(
            sessionId = sessionId,
            text = transcript.text,
        )
        if (result is Error) return NotRecognized

        return mqttConnection.incomingMessages
            .filterIsInstance<MqttIntentResult>()
            .filter { it.sessionId == sessionId }
            .map {
                when (it) {
                    is IntentRecognitionResult -> Intent(it.intentName, it.intent)
                    is IntentNotRecognized     -> NotRecognized
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