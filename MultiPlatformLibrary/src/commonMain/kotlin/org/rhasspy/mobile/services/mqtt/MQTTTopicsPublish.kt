package org.rhasspy.mobile.services.mqtt

/**
 * topics where the mqtt client will publish to
 */
enum class MQTTTopicsPublish(val topic: String) {

    SessionStarted("hermes/dialogueManager/sessionStarted"),
    SessionEnded("hermes/dialogueManager/sessionEnded"),
    SessionNotIntentRecognized("hermes/dialogueManager/intentNotRecognized"),
    AsrStartListening( "hermes/asr/startListening"),
    AsrStopListening("hermes/asr/stopListening"),
    AsrAudioSessionFrame("hermes/audioServer/<siteId>/audioFrame"),
    WakeWordDetected("hermes/hotword/default/detected"),
    AudioCaptured("hermes/asr/<siteId>/<sessionId>/audioCaptured"),
    IntentRecognition("hermes/nlu/query"),
    Say("hermes/tts/say"),
    AudioOutputPlayBytes("hermes/audioServer/<siteId>/playBytes/#"),
    AudioOutputPlayFinished("hermes/audioServer/<siteId>/playFinished");
}