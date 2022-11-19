package org.rhasspy.mobile.services.httpclient.data

import org.rhasspy.mobile.services.state.StateType

enum class HttpClientCallType : StateType {
    SpeechToText,
    IntentRecognition,
    TextToSpeech,
    PlayWav,
    IntentHandling,
    HassEvent,
    HassIntent
}