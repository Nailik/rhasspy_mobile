package org.rhasspy.mobile.services.mqtt

/**
 * contains all mqtt topics that the mqtt service subscribes to
 */
enum class MQTTTopicsSubscription(val topic: String) {

    StartSession("hermes/dialogueManager/startSession"),
    EndSession("hermes/dialogueManager/endSession"),
    HotWordToggleOn("hermes/hotword/toggleOn"),
    HotWordToggleOff("hermes/hotword/toggleOff"),
    AsrStartListening( "hermes/asr/startListening"),
    AsrStopListening("hermes/asr/stopListening"),
    AsrTextCaptured("hermes/asr/textCaptured"),
    AsrError("hermes/error/asr"),
    IntentRecognitionResult("hermes/intent/+"),
    IntentNotRecognized("hermes/nlu/intentNotRecognized"),
    IntentHandlingToggleOn("hermes/handle/toggleOn"),
    IntentHandlingToggleOff("hermes/handle/toggleOff"),
    SayFinished("hermes/tts/sayFinished"),
    AudioOutputToggleOff("hermes/audioServer/toggleOff"),
    AudioOutputToggleOn("hermes/audioServer/toggleOn"),
    SetVolume("rhasspy/audioServer/setVolume")
}