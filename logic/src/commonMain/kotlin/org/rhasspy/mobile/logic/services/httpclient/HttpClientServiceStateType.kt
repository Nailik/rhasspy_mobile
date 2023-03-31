package org.rhasspy.mobile.logic.services.httpclient

import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.service.ServiceState

enum class HttpClientServiceStateType(val serviceState: ServiceState) {
    IllegalArgumentException(ServiceState.Error(MR.strings.illegal_argument_exception)),
    InvalidTLSRecordType(ServiceState.Error(MR.strings.invalid_tls_record_type)), // Invalid TLS record type code: 72)
    UnresolvedAddressException(ServiceState.Error(MR.strings.unresolved_address_exception)), //server cannot be reached
    ConnectException(ServiceState.Error(MR.strings.connection_exception)),
    ConnectionRefused(ServiceState.Error(MR.strings.connection_refused))
}