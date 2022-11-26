package org.rhasspy.mobile.middleware

import org.rhasspy.mobile.mqtt.MqttError

sealed interface EventType {

    enum class WebServerEventType : EventType {
        Start,
        IncomingCall
    }

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


sealed interface ErrorType {

    sealed class MqttServiceErrorType : ErrorType {
        object NotInitialized : MqttServiceErrorType()
        object InvalidVolume : MqttServiceErrorType()
        object InvalidTopic : MqttServiceErrorType()
        class PublishError(mqttError: MqttError) : MqttServiceErrorType()
        class SubscriptionError(mqttError: MqttError) : MqttServiceErrorType()
        class ConnectionError(mqttError: MqttError) : MqttServiceErrorType()
        object AlreadyConnected : MqttServiceErrorType()
    }

    enum class HttpClientServiceErrorType(val description: String) : ErrorType {
        NotInitialized(""),
        IllegalArgumentException("IllegalArgumentException"),
        InvalidTLSRecordType("Invalid TLS record type code: 72"), // Invalid TLS record type code: 72)
        UnresolvedAddressException("UnresolvedAddressException"), //server cannot be reached
        ConnectException("ConnectException"),
        ConnectionRefused("Connection refused") //wrong port or address
    }

    enum class RhasspyActionsServiceErrorType : ErrorType {
        Disabled,
        NotInitialized
    }

    enum class UdpServiceErrorType : ErrorType {
        NotInitialized
    }

    enum class WebServerServiceErrorType(val description: String) : ErrorType {
        WakeOptionInvalid("Invalid value, allowed: \"on\", \"off\""),
        VolumeValueOutOfRange("Volume Out of Range, allowed: 0f...1f"),
        VolumeValueInvalid("Invalid Volume, allowed: 0f...1f"),
        AudioContentTypeWarning("Missing Content Type")
    }

    enum class HotWordServiceError : ErrorType {
        MicrophonePermissionMissing,
        PorcupineActivationException,
        PorcupineActivationLimitException,
        PorcupineActivationRefusedException,
        PorcupineActivationThrottledException,
        PorcupineInvalidArgumentException,
        PorcupineInvalidStateException,
        PorcupineIOException,
        PorcupineKeyException,
        PorcupineMemoryException,
        PorcupineRuntimeException,
        PorcupineStopIterationException,
        Unknown
    }

    enum class IndicationServiceErrorType : ErrorType {
        OverlayPermissionMissing
    }

}