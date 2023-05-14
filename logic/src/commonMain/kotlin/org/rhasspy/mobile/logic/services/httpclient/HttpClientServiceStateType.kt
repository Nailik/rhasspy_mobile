package org.rhasspy.mobile.logic.services.httpclient

import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.resources.MR

enum class HttpClientServiceStateType(val serviceState: ServiceState) {
    IllegalArgumentException(ServiceState.Error(MR.strings.illegal_argument_exception.stable)),
    InvalidTLSRecordType(ServiceState.Error(MR.strings.invalid_tls_record_type.stable)), // Invalid TLS record type code: 72)
    UnresolvedAddressException(ServiceState.Error(MR.strings.unresolved_address_exception.stable)), //server cannot be reached
    ConnectException(ServiceState.Error(MR.strings.connection_exception.stable)),
    ConnectionRefused(ServiceState.Error(MR.strings.connection_refused.stable))
}