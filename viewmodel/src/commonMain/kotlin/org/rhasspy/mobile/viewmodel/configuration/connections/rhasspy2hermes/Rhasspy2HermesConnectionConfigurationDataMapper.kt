package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes

import org.rhasspy.mobile.data.connection.HttpConnectionData
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationViewState.HttpConnectionConfigurationData

class Rhasspy2HermesConnectionConfigurationDataMapper {

    operator fun invoke(data: HttpConnectionData): HttpConnectionConfigurationData {
        return HttpConnectionConfigurationData(
            host = data.host,
            timeout = data.timeout,
            bearerToken = data.bearerToken,
            isSSLVerificationDisabled = data.isSSLVerificationDisabled,
        )
    }

    operator fun invoke(data: HttpConnectionConfigurationData): HttpConnectionData {
        return HttpConnectionData(
            host = data.host,
            timeout = data.timeout,
            bearerToken = data.bearerToken,
            isSSLVerificationDisabled = data.isSSLVerificationDisabled,
        )
    }

}