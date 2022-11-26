package org.rhasspy.mobile.middleware

import org.rhasspy.mobile.mqtt.MqttError

sealed interface EventType {

    enum class HotWordServiceEventType : EventType {
        StartPorcupine,
        Detected
    }

    enum class HttpClientServiceEventType : EventType {
        Start,
        SpeechToText,
        RecognizeIntent,
        TextToSpeech,
        PlayWav,
        IntentHandling,
        HassEvent,
        HassIntent
    }

    enum class WebServerServiceEventType : EventType {
        Start,
        Received
    }

    enum class UdpServiceEventType : EventType {
        Start,
        StreamAudio
    }

    enum class MqttServiceEventType : EventType {
        Start,
        Connecting,
        Disconnect,
        Reconnect,
        SubscribeTopic,
        Subscribing,
        Publish,
        Received
    }

    enum class RhasspyActionServiceEventType : EventType {
        RecognizeIntent,
        Say,
        PlayAudio,
        SpeechToText,
        IntentHandling
    }

    enum class HomeAssistantServiceEventType : EventType {
        SendIntent
    }

    enum class IndicationServiceEventType : EventType {
        Start
    }

}

