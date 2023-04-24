package org.rhasspy.mobile.logic.services.homeassistant

import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.koin.core.component.inject
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption.Event
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption.Intent
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService

/**
 * send data to home assistant
 *
 * events
 * intent
 */
class HomeAssistantService(
    paramsCreator: HomeAssistantServiceParamsCreator,
) : IService(LogType.HomeAssistanceService) {

    private val httpClientService by inject<HttpClientService>()

    private val paramsFlow: StateFlow<HomeAssistantServiceParams> = paramsCreator()
    private val params: HomeAssistantServiceParams get() = paramsFlow.value

    /**
     * simplified conversion from intent to hass event or hass intent
     */
    suspend fun sendIntent(intentName: String, intent: String): ServiceState {
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
            slots["_site_id"] = JsonPrimitive(params.siteId)

            val intentRes = Json.encodeToString(slots)

            val result = when (params.intentHandlingHomeAssistantOption) {
                Event -> httpClientService.hassEvent(intentRes, intentName)
                Intent -> httpClientService.hassIntent("{\"name\" : \"$intentName\", \"data\": $intent }")
            }
            return result.toServiceState()
        } catch (exception: Exception) {
            logger.e(exception) { "sendIntent error" }
            return ServiceState.Exception(exception)
        }
    }
}