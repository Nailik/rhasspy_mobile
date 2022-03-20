package org.rhasspy.mobile.services.mqtt

/**
 * contains all mqtt topics that the mqtt service subscribes to
 */
enum class MQTTTopicsSubscription(val topic: String) {

    StartSession("hermes/dialogueManager/startSession"),
    EndSession("hermes/dialogueManager/endSession"),
    SessionStarted("hermes/dialogueManager/sessionStarted"),
    SessionEnded("hermes/dialogueManager/sessionEnded"),
    HotWordToggleOn("hermes/hotword/toggleOn"),
    HotWordToggleOff("hermes/hotword/toggleOff"),
    HotWordDetected("hermes/hotword/+/detected"),
    AsrStartListening("hermes/asr/startListening"),
    AsrStopListening("hermes/asr/stopListening"),
    AsrTextCaptured("hermes/asr/textCaptured"),
    AsrError("hermes/error/asr"),
    IntentRecognitionResult("hermes/intent/+"),
    IntentNotRecognized("hermes/nlu/intentNotRecognized"),
    IntentHandlingToggleOn("hermes/handle/toggleOn"),
    IntentHandlingToggleOff("hermes/handle/toggleOff"),
    PlayBytes("hermes/audioServer/<siteId>/playBytes/+"),
    AudioOutputToggleOff("hermes/audioServer/toggleOff"),
    AudioOutputToggleOn("hermes/audioServer/toggleOn"),
    SetVolume("rhasspy/audioServer/setVolume");

    companion object {
        fun fromTopic(topic: String): MQTTTopicsSubscription? {
            return values().firstOrNull { it.topic == topic }
        }
    }

}