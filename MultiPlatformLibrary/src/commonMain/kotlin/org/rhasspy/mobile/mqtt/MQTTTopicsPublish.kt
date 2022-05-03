package org.rhasspy.mobile.mqtt

/**
 * topics where the mqtt client will publish to
 */
enum class MQTTTopicsPublish(val topic: String) {

    SessionStarted("hermes/dialogueManager/sessionStarted"),
    SessionEnded("hermes/dialogueManager/sessionEnded"),
    IntentNotRecognizedInSession("hermes/dialogueManager/intentNotRecognized"),
    AsrStartListening("hermes/asr/startListening"),
    AsrStopListening("hermes/asr/stopListening"),
    AsrTextCaptured("hermes/asr/textCaptured"),
    AsrError("hermes/error/asr"),
    AudioCaptured("rhasspy/asr/<siteId>/<sessionId>/audioCaptured"),
    AsrAudioFrame("hermes/audioServer/<siteId>/audioFrame"),
    HotWordDetected("hermes/hotword/default/detected"),
    HotWordError("hermes/error/hotword"),
    Query("hermes/nlu/query"),
    Say("hermes/tts/say"),
    AudioOutputPlayBytes("hermes/audioServer/<siteId>/playBytes/<requestId>"),
    AudioOutputPlayFinished("hermes/audioServer/<siteId>/playFinished");
}