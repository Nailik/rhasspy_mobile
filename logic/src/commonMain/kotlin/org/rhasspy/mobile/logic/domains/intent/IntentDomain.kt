package org.rhasspy.mobile.logic.domains.intent

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.domain.IntentDomainData
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IntentDomainOption
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.EndSession
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult.IntentNotRecognized
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult.IntentRecognitionResult
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.Say
import org.rhasspy.mobile.logic.connections.mqtt.MqttResult.Error
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent.WebServerSay
import org.rhasspy.mobile.logic.domains.IDomainHistory
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.pipeline.HandleResult.Handle
import org.rhasspy.mobile.logic.pipeline.IntentResult
import org.rhasspy.mobile.logic.pipeline.IntentResult.Intent
import org.rhasspy.mobile.logic.pipeline.IntentResult.IntentError
import org.rhasspy.mobile.logic.pipeline.Source.*
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.Transcript
import org.rhasspy.mobile.logic.pipeline.domain.Reason
import org.rhasspy.mobile.platformspecific.timeoutWithDefault
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.IntentResult as MqttIntentResult

/**
 * IntentDomain recognizes an intent from a transcript
 */
internal interface IIntentDomain : IDomain {

    /**
     * sends Text and waits for an IntentResult result
     */
    suspend fun awaitIntent(transcript: Transcript): IntentResult

}

/**
 * IntentDomain recognizes an intent from a transcript
 */
internal class IntentDomain(
    private val params: IntentDomainData,
    private val mqttConnection: IMqttConnection,
    private val webServerConnection: IWebServerConnection,
    private val rhasspy2HermesConnection: IRhasspy2HermesConnection,
    private val indication: IIndication,
    private val domainHistory: IDomainHistory,
) : IIntentDomain {

    private val logger = Logger.withTag("IntentDomain")

    /**
     * sends Text and waits for an IntentResult result
     */
    override suspend fun awaitIntent(transcript: Transcript): IntentResult {
        logger.d { "awaitIntent for transcript $transcript" }
        indication.onThinking()

        return when (params.option) {
            IntentDomainOption.Rhasspy2HermesHttp ->
                awaitRhasspy2HermesHttpIntent(
                    transcript = transcript,
                )

            IntentDomainOption.Rhasspy2HermesMQTT ->
                awaitRhasspy2HermesMQTTIntent(
                    transcript = transcript,
                )

            IntentDomainOption.Disabled           ->
                IntentError(
                    sessionId = transcript.sessionId,
                    reason = Reason.Disabled,
                    source = Local,
                )
        }.also {
            domainHistory.addToHistory(transcript, it)
        }
    }

    /**
     * sends Text to Rhasspy2HermesHttp
     * if NOT isRhasspy2HermesHttpHandleWithRecognition it returns the intent result
     * else it awaits end session or say from mqtt or say from webserver
     */
    private suspend fun awaitRhasspy2HermesHttpIntent(transcript: Transcript): IntentResult {
        logger.d { "awaitIntent for transcript $transcript" }

        return when (val result = rhasspy2HermesConnection.recognizeIntent(transcript.text)) {
            is HttpClientResult.HttpClientError ->
                IntentError(
                    sessionId = transcript.sessionId,
                    reason = Reason.Error(result.message),
                    source = Rhasspy2HermesHttp,
                )

            is HttpClientResult.Success         -> {
                val intentName = readIntentNameFromJson(result.data)

                if (!params.isRhasspy2HermesHttpHandleWithRecognition) {
                    return Intent(
                        intentName = intentName,
                        intent = result.data,
                        sessionId = transcript.sessionId,
                        source = Rhasspy2HermesHttp,
                    )
                }

                //await for EndSession or Say
                return merge(
                    mqttConnection.incomingMessages
                        .filterIsInstance<EndSession>()
                        .filter { it.sessionId == transcript.sessionId }
                        .map {
                            Handle(
                                text = it.text,
                                volume = null,
                                sessionId = transcript.sessionId,
                                source = Rhasspy2HermesMqtt,
                            )
                        },
                    mqttConnection.incomingMessages
                        .filterIsInstance<Say>()
                        .filter { it.sessionId == transcript.sessionId }
                        .filter { it.siteId == ConfigurationSetting.siteId.value }
                        .map {
                            Handle(
                                text = it.text,
                                volume = it.volume,
                                sessionId = transcript.sessionId,
                                source = Rhasspy2HermesMqtt,
                            )
                        },
                    webServerConnection.incomingMessages
                        .filterIsInstance<WebServerSay>()
                        .map {
                            Handle(
                                text = it.text,
                                volume = null,
                                sessionId = transcript.sessionId,
                                source = WebServer,
                            )
                        },
                ).timeoutWithDefault(
                    timeout = params.timeout,
                    default = IntentError(
                        sessionId = transcript.sessionId,
                        reason = Reason.Timeout,
                        source = Local,
                    ),
                ).first()
            }
        }
    }

    /**
     * sends Text to Rhasspy2HermesMqtt
     * awaits for MqttIntentResult or timeout
     */
    private suspend fun awaitRhasspy2HermesMQTTIntent(transcript: Transcript): IntentResult {
        logger.d { "awaitIntent for transcript $transcript" }
        val result = mqttConnection.recognizeIntent(
            sessionId = transcript.sessionId,
            text = transcript.text,
        )
        if (result is Error) return IntentError(
            sessionId = transcript.sessionId,
            reason = Reason.Error(result.message),
            source = Rhasspy2HermesMqtt,
        )

        return mqttConnection.incomingMessages
            .filterIsInstance<MqttIntentResult>()
            .filter { it.sessionId == transcript.sessionId }
            .map {
                when (it) {
                    is IntentRecognitionResult ->
                        Intent(
                            intentName = it.intentName,
                            intent = it.intent,
                            sessionId = transcript.sessionId,
                            source = Rhasspy2HermesMqtt,
                        )

                    is IntentNotRecognized     ->
                        IntentError(
                            sessionId = transcript.sessionId,
                            reason = Reason.Error(MR.strings.intent_not_recognized.stable),
                            source = Rhasspy2HermesMqtt,
                        )
                }
            }.timeoutWithDefault(
                timeout = params.timeout,
                default = IntentError(
                    sessionId = transcript.sessionId,
                    reason = Reason.Timeout,
                    source = Local,
                ),
            ).first()
    }

    override fun dispose() {}

    /**
     * read the intent name from inten json
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