package org.rhasspy.mobile.logic.connections.homeassistant

import co.touchlab.kermit.Logger
import io.ktor.client.request.setBody
import io.ktor.client.utils.buildHeaders
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.logic.connections.IConnection
import org.rhasspy.mobile.logic.connections.http.IHttpConnection
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IHomeAssistantConnection : IConnection {

    suspend fun awaitIntent(
        intentName: String?,
        intent: String,
    ): HttpClientResult<String?>

    suspend fun awaitEvent(
        intentName: String?,
        intent: String,
    ): HttpClientResult<String>

}

/**
 * send data to home assistant
 *
 * events
 * intent
 */
internal class HomeAssistantConnection : IHomeAssistantConnection, IHttpConnection(ConfigurationSetting.homeAssistantConnection) {

    override val logger = Logger.withTag("HomeAssistantConnection")

    override suspend fun awaitIntent(intentName: String?, intent: String): HttpClientResult<String?> {
        val data = jsonFromIntent(intent)
        logger.d { "homeAssistantIntent intent: $data intentName: $intentName" }

        val result = post<String?>(
            url = "/api/intent/handle",
            block = {
                buildHeaders {
                    contentType(jsonContentType)
                }
                setBody("{\"name\" : \"$intentName\", \"data\": $data }")
            },
        )

        return when (result) {
            is HttpClientResult.HttpClientError -> return result
            is HttpClientResult.Success         -> HttpClientResult.Success(readSpeechTextFromIntentResponse(result.data))
        }

    }

    override suspend fun awaitEvent(intentName: String?, intent: String): HttpClientResult<String> {
        val data = jsonFromIntent(intent)
        logger.d { "homeAssistantEvent intent: $data intentName: $intentName" }

        return post(
            url = "/api/events/rhasspy_$intentName",
            block = {
                buildHeaders {
                    contentType(jsonContentType)
                }
                setBody(data)
            },
        )
    }

    private fun readSpeechTextFromIntentResponse(intent: String?): String? {
        if (intent == null) return null
        //reads text from intent api https://developers.home-assistant.io/docs/intent_firing
        return try {
            //example: "{"speech": {"plain": {"speech": "Turned Lounge Lamp on", "extra_data": null}}, "card": {}}"
            val json = Json.decodeFromString<JsonObject>(intent)
            return json["speech"]?.jsonObject?.get("plain")?.jsonObject?.getValue("speech")?.jsonPrimitive?.contentOrNull
        } catch (exception: Exception) {
            logger.e(exception) { "sendIntent error" }
            null
        }
    }

    private fun jsonFromIntent(intent: String): String? {
        return try {
            val slots = mutableMapOf<String, JsonElement?>()

            val json = Json.decodeFromString<JsonObject>(intent)

            //for slot in nlu_intent.slots:
            val jsonSlots = json["slots"]
            if (jsonSlots is JsonArray) {
                //converts json array of slots from mqtt
                json["slots"]?.jsonArray?.forEach { element ->
                    val slotName = element.jsonObject["slotName"]?.jsonPrimitive?.content

                    if (slotName?.isNotEmpty() == true) {
                        slots[slotName] = element.jsonObject["value"]?.jsonObject?.get("value")
                    }
                }
            } else if (jsonSlots is JsonObject) {
                //converts json object of slots from http
                json["slots"]?.jsonObject?.entries?.forEach {
                    slots[it.key] = it.value.jsonPrimitive
                }
            }

            //add meta slots
            slots["_text"] = json["text"]?.jsonPrimitive
            slots["_raw_text"] = json["raw_text"]?.jsonPrimitive
            slots["_site_id"] = JsonPrimitive(ConfigurationSetting.siteId.value)

            Json.encodeToString(slots)
        } catch (exception: Exception) {
            logger.e(exception) { "sendIntent error" }
            null
        }
    }

}