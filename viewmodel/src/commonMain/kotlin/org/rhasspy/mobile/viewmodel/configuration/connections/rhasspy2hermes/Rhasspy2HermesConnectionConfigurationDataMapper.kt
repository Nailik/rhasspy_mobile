package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes

import org.rhasspy.mobile.data.connection.HttpConnectionData
import org.rhasspy.mobile.data.data.toIntOrZero
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationViewState.Rhasspy2HermesConnectionConfigurationData
import kotlin.time.Duration.Companion.seconds

class Rhasspy2HermesConnectionConfigurationDataMapper {

    operator fun invoke(data: HttpConnectionData): Rhasspy2HermesConnectionConfigurationData {
        return Rhasspy2HermesConnectionConfigurationData(
            host = data.host,
            timeout = data.timeout.inWholeSeconds.toString(),
            bearerToken = data.bearerToken,
            isSSLVerificationDisabled = data.isSSLVerificationDisabled,
        )
    }

    operator fun invoke(data: Rhasspy2HermesConnectionConfigurationData): HttpConnectionData {
        return HttpConnectionData(
            host = data.host,
            timeout = data.timeout.toIntOrZero().seconds,
            bearerToken = data.bearerToken,
            isSSLVerificationDisabled = data.isSSLVerificationDisabled,
        )
    }

}