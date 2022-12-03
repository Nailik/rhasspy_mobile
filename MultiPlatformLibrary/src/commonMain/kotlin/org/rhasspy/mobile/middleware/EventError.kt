package org.rhasspy.mobile.middleware

import org.rhasspy.mobile.mqtt.MqttError


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
        AudioContentTypeWarning("Missing Content Type");

        override fun toString(): String {
            return description
        }
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
        Unknown,
        NotInitialized
    }

    enum class IndicationServiceErrorType : ErrorType {
        OverlayPermissionMissing
    }


    enum class RecordingServiceErrorType : ErrorType {
        NotInitialized
    }


}