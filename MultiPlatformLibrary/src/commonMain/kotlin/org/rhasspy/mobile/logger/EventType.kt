package org.rhasspy.mobile.logger

enum class EventType {
    WebServerStart,
    WebServerIncomingCall,
    HttpClientStart,
    HttpClientSpeechToText,
    HttpClientRecognizeIntent,
    HttpClientTextToSpeech,
    HttpClientPlayWav,
    HttpClientIntentHandling,
    HttpClientHassEvent,
    HttpClientHassIntent
}