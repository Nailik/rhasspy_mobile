package org.rhasspy.mobile.services.homeassistant

import co.touchlab.kermit.Logger
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.koin.core.component.inject
import org.rhasspy.mobile.settings.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.httpclient.HttpClientService

//TODO logging
/**
 * send data to home assistant
 *
 * events
 * intent
 */
class HomeAssistantService : IService() {

    private val logger = Logger.withTag("HomeAssistantInterface")

    private val params by inject<HomeAssistantServiceParams>()
    private val httpClientService by inject<HttpClientService>()

    override fun onClose() {
        //nothing to close
    }

    /**
     * simplified conversion from intent to hass event or hass intent
     */
    suspend fun sendIntent(intentName: String, intent: String) {
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
            slots["_site_id"] = JsonPrimitive(params.siteId)

            val intentRes = Json.encodeToString(slots)

            when (params.intentHandlingHomeAssistantOption) {
                HomeAssistantIntentHandlingOption.Event -> httpClientService.hassEvent(intentRes, intentName)
                HomeAssistantIntentHandlingOption.Intent -> httpClientService.hassIntent("{\"name\" : \"$intentName\", \"data\": $intent }")
            }
        } catch (exception: Exception) {
            logger.e(exception) { "" }
        }
    }
}