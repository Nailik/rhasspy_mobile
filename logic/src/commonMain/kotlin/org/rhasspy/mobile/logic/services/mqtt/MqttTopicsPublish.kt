package org.rhasspy.mobile.logic.services.mqtt

/**
 * topics where the mqtt client will publish to
 */
enum class MqttTopicsPublish(override val topic: String) : MqttTopic {

    SessionStarted("hermes/dialogueManager/sessionStarted"),
    SessionEnded("hermes/dialogueManager/sessionEnded"),
    IntentNotRecognizedInSession("hermes/dialogueManager/intentNotRecognized"),
    AsrStartListening("hermes/asr/startListening"),
    AsrStopListening("hermes/asr/stopListening"),
    AsrTextCaptured("hermes/asr/textCaptured"),
    AsrError("hermes/error/asr"),
    AudioCaptured("rhasspy/asr/${MqttTopicPlaceholder.SiteId}/${MqttTopicPlaceholder.SessionId}/audioCaptured"),
    AsrAudioFrame("hermes/audioServer/${MqttTopicPlaceholder.SiteId}/audioFrame"),
    AsrAudioSessionFrame("hermes/audioServer/${MqttTopicPlaceholder.SiteId}/${MqttTopicPlaceholder.SessionId}/audioSessionFrame"),
    HotWordDetected("hermes/hotword/${MqttTopicPlaceholder.WakeWord}/detected"),
    WakeWordError("hermes/error/hotword"),
    Query("hermes/nlu/query"),
    Say("hermes/tts/say"),
    AudioOutputPlayBytes("hermes/audioServer/${MqttTopicPlaceholder.SiteId}/playBytes/<requestId>"),
    AudioOutputPlayFinished("hermes/audioServer/${MqttTopicPlaceholder.SiteId}/playFinished");

}