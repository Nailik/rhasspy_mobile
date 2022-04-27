package org.rhasspy.mobile.services

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.rhasspy.mobile.settings.ConfigurationSettings

object HomeAssistantService {

    /**
     * simplified conversion from intent to hass event or hass intent
     */
    suspend fun sendIntent(intent: String) {
        val slots = mutableMapOf<String, JsonPrimitive?>()

        val json = Json.decodeFromString<JsonObject>(intent)

        val intentName = json["intent"]?.jsonObject?.get("name")?.jsonPrimitive?.content ?: ""

        //for slot in nlu_intent.slots:
        json["slots"]?.jsonObject?.entries?.forEach {
            slots[it.key] = it.value.jsonPrimitive
        }

        //add meta slots
        slots["_text"] = json["text"]?.jsonPrimitive
        slots["_raw_text"] = json["raw_text"]?.jsonPrimitive
        slots["_site_id"] = JsonPrimitive(ConfigurationSettings.siteId.data)

        val intentRes = Json.encodeToString(slots)

        when (ConfigurationSettings.isIntentHandlingHassEvent.data) {
            true -> HttpService.hassEvent(intentRes, intentName)
            false -> HttpService.hassIntent(intent)
        }
    }
}