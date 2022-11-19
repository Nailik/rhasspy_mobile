package org.rhasspy.mobile.serviceInterfaces

import co.touchlab.kermit.Logger
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.settings.ConfigurationSettings

/**
 * used to send intents or events to home assistant
 */
object HomeAssistantInterface : KoinComponent {

    val logger = Logger.withTag("HomeAssistantInterface")

    private val httpClientService by inject<HttpClientService>()

    /**
     * simplified conversion from intent to hass event or hass intent
     */
    suspend fun sendIntent(intentName: String, intent: String) {
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
        slots["_site_id"] = JsonPrimitive(ConfigurationSettings.siteId.value)

        val intentRes = Json.encodeToString(slots)

        when (ConfigurationSettings.isIntentHandlingHassEvent.value) {
            true -> httpClientService.hassEvent(intentRes, intentName)
            false -> httpClientService.hassIntent("{\"name\" : \"$intentName\", \"data\": $intent }")
        }
    }
}