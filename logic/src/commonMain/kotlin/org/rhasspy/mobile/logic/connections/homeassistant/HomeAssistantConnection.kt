package org.rhasspy.mobile.logic.connections.homeassistant

import io.ktor.client.request.setBody
import io.ktor.client.utils.buildHeaders
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.ErrorState
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption.Event
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption.Intent
import org.rhasspy.mobile.logic.connections.IConnection
import org.rhasspy.mobile.logic.connections.IHttpConnection
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IHomeAssistantConnection : IConnection {

    fun sendIntent(intentName: String, intent: String, onResult: (result: ServiceState) -> Unit)

}

/**
 * send data to home assistant
 *
 * events
 * intent
 */
internal class HomeAssistantConnection : IHomeAssistantConnection, IHttpConnection(ConfigurationSetting.homeAssistantConnection) {

    override val logger = LogType.HomeAssistanceService.logger()

    /**
     * simplified conversion from intent to hass event or hass intent
     */
    override fun sendIntent(intentName: String, intent: String, onResult: (result: ServiceState) -> Unit) {
        logger.d { "sendIntent name: $intentName json: $intent" }
        try {
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

            val intentRes = Json.encodeToString(slots)

            when (ConfigurationSetting.intentHandlingHomeAssistantOption.value) {
                Event  -> homeAssistantEvent(intentRes, intentName) {
                    onResult(it.toServiceState())
                }

                Intent -> homeAssistantIntent("{\"name\" : \"$intentName\", \"data\": $intent }") {
                    onResult(it.toServiceState())
                }
            }
        } catch (exception: Exception) {
            logger.e(exception) { "sendIntent error" }
            onResult(ErrorState.Exception(exception))
        }
    }


    /**
     * send intent as Event to Home Assistant
     */
    private fun homeAssistantEvent(json: String, intentName: String, onResult: (result: HttpClientResult<String>) -> Unit) {
        logger.d { "homeAssistantEvent json: $json intentName: $intentName" }
        post(
            url = "/api/events/rhasspy_$intentName",
            block = {
                buildHeaders {
                    contentType(jsonContentType)
                }
                setBody(json)
            },
            onResult = onResult
        )
    }


    /**
     * send intent as Intent to Home Assistant
     */
    private fun homeAssistantIntent(intentJson: String, onResult: (result: HttpClientResult<String>) -> Unit) {
        logger.d { "homeAssistantIntent json: $intentJson" }
        post(
            url = "/api/intent/handle",
            block = {
                buildHeaders {
                    contentType(jsonContentType)
                }
                setBody(intentJson)
            },
            onResult = onResult
        )
    }

}