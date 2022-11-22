package org.rhasspy.mobile.logger

enum class EventType {
    WebServerStart,
    WebServerIncomingCall,
    HttpClientStart,
    HttpClientSpeechToText,
    RecognizeIntent,
    HttpClientTextToSpeech
}