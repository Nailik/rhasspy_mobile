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

    sealed class WakeWordServiceError(val exception: Exception?) : ErrorType {
        class MicrophonePermissionMissing(exception: Exception) : WakeWordServiceError(exception)
        class PorcupineActivationException(exception: Exception) : WakeWordServiceError(exception)
        class PorcupineActivationLimitException(exception: Exception) : WakeWordServiceError(exception)
        class PorcupineActivationRefusedException(exception: Exception) : WakeWordServiceError(exception)
        class PorcupineActivationThrottledException(exception: Exception) : WakeWordServiceError(exception)
        class PorcupineInvalidArgumentException(exception: Exception) : WakeWordServiceError(exception)
        class PorcupineInvalidStateException(exception: Exception) : WakeWordServiceError(exception)
        class PorcupineIOException(exception: Exception) : WakeWordServiceError(exception)
        class PorcupineKeyException(exception: Exception) : WakeWordServiceError(exception)
        class PorcupineMemoryException(exception: Exception) : WakeWordServiceError(exception)
        class PorcupineRuntimeException(exception: Exception) : WakeWordServiceError(exception)
        class PorcupineStopIterationException(exception: Exception) : WakeWordServiceError(exception)
        class Other(exception: Exception) : WakeWordServiceError(exception)
        object NotInitialized : WakeWordServiceError(null)
    }

    enum class IndicationServiceErrorType : ErrorType {
        OverlayPermissionMissing
    }


    enum class RecordingServiceErrorType : ErrorType {
        NotInitialized
    }


}