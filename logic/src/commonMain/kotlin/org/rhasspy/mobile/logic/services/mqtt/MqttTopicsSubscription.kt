package org.rhasspy.mobile.logic.services.mqtt

/**
 * contains all mqtt topics that the mqtt service subscribes to
 */
enum class MqttTopicsSubscription(override val topic: String) : MqttTopic {

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
    PlayBytes("hermes/audioServer/${MqttTopicPlaceholder.SiteId}/playBytes/+"),
    PlayFinished("hermes/audioServer/${MqttTopicPlaceholder.SiteId}/playFinished"),
    AudioOutputToggleOn("hermes/audioServer/toggleOn"),
    AudioOutputToggleOff("hermes/audioServer/toggleOff"),
    Say("hermes/tts/say"),
    SetVolume("rhasspy/audioServer/setVolume");

    override fun toString(): String {
        return topic
    }

    companion object {
        fun fromTopic(topic: String): MqttTopicsSubscription? {
            return values().firstOrNull { it.topic == topic }
        }
    }

}