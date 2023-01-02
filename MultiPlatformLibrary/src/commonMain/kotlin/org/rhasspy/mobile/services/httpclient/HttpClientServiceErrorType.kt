package org.rhasspy.mobile.services.httpclient

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class HttpClientServiceErrorType(val description: String, val humanReadable: StringResource) {
    IllegalArgumentException("IllegalArgumentException", MR.strings.defaultText),
    InvalidTLSRecordType("Invalid TLS record type code: 72", MR.strings.defaultText), // Invalid TLS record type code: 72)
    UnresolvedAddressException("UnresolvedAddressException", MR.strings.defaultText), //server cannot be reached
    ConnectException("ConnectException", MR.strings.defaultText),
    ConnectionRefused("Connection refused", MR.strings.defaultText) //wrong port or address
}