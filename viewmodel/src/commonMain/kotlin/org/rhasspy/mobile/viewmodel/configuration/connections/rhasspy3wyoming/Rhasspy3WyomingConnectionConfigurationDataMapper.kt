package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming

import org.rhasspy.mobile.data.connection.HttpConnectionData
import org.rhasspy.mobile.data.data.toIntOrZero
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationViewState.Rhasspy3WyomingConnectionConfigurationData
import kotlin.time.Duration.Companion.seconds

class Rhasspy3WyomingConnectionConfigurationDataMapper {

    operator fun invoke(data: HttpConnectionData): Rhasspy3WyomingConnectionConfigurationData {
        return Rhasspy3WyomingConnectionConfigurationData(
            host = data.host,
            timeout = data.timeout.inWholeSeconds.toString(),
            bearerToken = data.bearerToken,
            isSSLVerificationDisabled = data.isSSLVerificationDisabled,
        )
    }

    operator fun invoke(data: Rhasspy3WyomingConnectionConfigurationData): HttpConnectionData {
        return HttpConnectionData(
            host = data.host,
            timeout = data.timeout.toIntOrZero().seconds,
            bearerToken = data.bearerToken,
            isSSLVerificationDisabled = data.isSSLVerificationDisabled,
        )
    }

}