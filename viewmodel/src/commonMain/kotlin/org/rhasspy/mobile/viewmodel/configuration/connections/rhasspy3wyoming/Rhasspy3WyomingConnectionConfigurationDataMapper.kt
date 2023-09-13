package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming

import org.rhasspy.mobile.data.connection.HttpConnectionData
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationViewState.Rhasspy3WyomingConnectionConfigurationData

class Rhasspy3WyomingConnectionConfigurationDataMapper {

    operator fun invoke(data: HttpConnectionData): Rhasspy3WyomingConnectionConfigurationData {
        return Rhasspy3WyomingConnectionConfigurationData(
            host = data.host,
            timeout = data.timeout,
            bearerToken = data.bearerToken,
            isSSLVerificationDisabled = data.isSSLVerificationDisabled,
        )
    }

    operator fun invoke(data: Rhasspy3WyomingConnectionConfigurationData): HttpConnectionData {
        return HttpConnectionData(
            host = data.host,
            timeout = data.timeout,
            bearerToken = data.bearerToken,
            isSSLVerificationDisabled = data.isSSLVerificationDisabled,
        )
    }

}