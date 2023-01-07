package org.rhasspy.mobile.middleware

import dev.icerock.moko.resources.StringResource

sealed class ServiceState() {

    object Pending : ServiceState()

    object Loading : ServiceState()

    object Success : ServiceState()

    class Warning(val information: StringResource) : ServiceState()

    class Exception(val exception: kotlin.Exception? = null) : ServiceState()

    class Error(val information: StringResource) : ServiceState()

    /*
        sealed class HttpClientServiceErrorType(exception: Throwable, val humanReadable: StringResource) : Error(exception) {
            class IllegalArgumentException(exception: Throwable) : HttpClientServiceErrorType(exception, MR.strings.defaultText)
            class InvalidTLSRecordType(exception: Throwable) : HttpClientServiceErrorType(exception, MR.strings.defaultText) // Invalid TLS record type code: 72)
            class UnresolvedAddressException(exception: Throwable) : HttpClientServiceErrorType(exception, MR.strings.defaultText) //server cannot be reached
            class ConnectException(exception: Throwable) : HttpClientServiceErrorType(exception, MR.strings.defaultText)
            class ConnectionRefused(exception: Throwable) : HttpClientServiceErrorType(exception, MR.strings.defaultText) //wrong port or address
            class Unknown(exception: Throwable) : HttpClientServiceErrorType(exception, MR.strings.defaultText)
        }

        class MqttServiceError(val mqttError: MqttError) : Error(Throwable())

        class Unknown(exception: Throwable) : Error(exception)
    }*/

    object Disabled : ServiceState()

}