package org.rhasspy.mobile.services

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.rhasspy.mobile.settings.ConfigurationSettings

object HomeAssistantService {

    @Suppress("UNUSED_PARAMETER")
    suspend fun sendIntent(intent: String) {

        //https://github.com/rhasspy/rhasspy-homeassistant-hermes/blob/master/rhasspyhomeassistant_hermes/__init__.py
        val slots = mutableMapOf<String, Any>()

        val json = Json.decodeFromString<JsonObject>(intent)

        val intentName = json["intent"]?.jsonObject?.get("name")?.jsonPrimitive?.content ?: ""

        //for slot in nlu_intent.slots:
        json["slots"]?.jsonObject?.entries?.forEach {
            //slots[slot.slot_name] = slot.value["value"]
            slots[it.key] = it.value.jsonPrimitive
/*
            val rawValue = it.value.jsonObject["raw_value"]
            //if slot.raw_value:
            if (rawValue != null) {
                //slots[slot.slot_name + "_raw_value"] = slot.raw_value
                slots["${it.key}_raw_value"] = rawValue.toString()
            }
*/
        }

        //add meta slots
        slots["_text"] = json["text"]?.jsonPrimitive?.content ?: "" // nlu_intent.input
        slots["_raw_text"] = json["raw_text"]?.jsonPrimitive?.content ?: "" // nlu_intent.raw_input
        slots["_intent"] = json["text"].toString() // nlu_intent.to_dict() ??
        slots["_site_id"] = ConfigurationSettings.siteId.data // nlu_intent.site_id

        val intentRes = Json.encodeToString(slots)

        when (ConfigurationSettings.isIntentHandlingHassEvent.data) {
            true -> HttpService.hassEvent(intentRes, intentName)
            false -> HttpService.hassIntent(intent)
        }
    }

}

fun JsonPrimitive.getRawContent(){

}
//TODO erklär text rhassp_
//Events will be named rhasspy_INTENT_NAME
//Requires the intent component and intent scripts in your configuration.yaml
//TODO link zu access token
//TODO convert correctly to event

/*
object
payload: object
event_type: "rhasspy_ChangeLightState"
entity_id: undefined
event: object
state: "aus"
state_raw_value: "aus"
_text: "mach das licht aus"
_raw_text: "mach das licht aus"
_intent: object
input: "mach das licht aus"
intent: object
intentName: "ChangeLightState"
confidenceScore: 1
siteId: "default"
id: "8bae2160-6f6b-4c24-9bb8-6b594f8acd6c"
slots: array[1]
0: object
entity: "state"
value: object
kind: "Unknown"
value: "aus"
slotName: "state"
rawValue: "aus"
confidence: 1
range: object
start: 15
end: 18
rawStart: 15
rawEnd: 18
sessionId: "8bae2160-6f6b-4c24-9bb8-6b594f8acd6c"
customData: null
asrTokens: array[1]
0: array[4]
0: object
1: object
2: object
3: object
asrConfidence: null
rawInput: "mach das licht aus"
wakewordId: null
lang: null
_site_id: "default"
origin: "REMOTE"
time_fired: "2022-03-25T00:19:13.800045+00:00"
context: object
id: "309df050f5269b125e7419fbadcac7ed"
parent_id: null
user_id: "29b8168e2f804d94b156ee08748a546a"
_msgid: "137ef48f29a9dcba"
 */


/*
object
payload: object
event_type: "rhasspy_ChangeLightState"
entity_id: undefined
event: object
entities: array[1]
0: object
end: 18
entity: "state"
raw_end: 18
raw_start: 15
raw_value: "aus"
start: 15
value: "aus"
value_details: object
intent: object
confidence: 1
name: "ChangeLightState"
raw_text: "mach das licht aus"
raw_tokens: array[4]
0: "mach"
1: "das"
2: "licht"
3: "aus"
recognize_seconds: 0.10598903696518391
slots: object
state: "aus"
speech_confidence: 1
text: "mach das licht aus"
tokens: array[4]
0: "mach"
1: "das"
2: "licht"
3: "aus"
wakeword_id: null
origin: "REMOTE"
time_fired: "2022-03-25T00:20:28.457566+00:00"
context: object
_msgid: "65795f8fda2b0a5e"
 */