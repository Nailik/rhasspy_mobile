package org.rhasspy.mobile.data.connection

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

enum class HttpClientErrorType(val text: StableStringResource) {
    IllegalArgumentError(MR.strings.illegal_argument_exception.stable),
    InvalidTLSRecordType(MR.strings.invalid_tls_record_type.stable), // Invalid TLS record type code: 72)
    UnresolvedAddressError(MR.strings.unresolved_address_exception.stable), //server cannot be reached
    ConnectError(MR.strings.connection_exception.stable),
    ConnectionRefused(MR.strings.connection_refused.stable)
}