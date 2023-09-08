package org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant

import org.rhasspy.mobile.data.connection.HttpConnectionData
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationViewState.HomeAssistantConnectionConfigurationData

class HomeAssistantConnectionConfigurationDataMapper {

    operator fun invoke(data: HttpConnectionData): HomeAssistantConnectionConfigurationData {
        return HomeAssistantConnectionConfigurationData(
            host = data.host,
            timeout = data.timeout,
            bearerToken = data.bearerToken,
            isSSLVerificationDisabled = data.isSSLVerificationDisabled,
        )
    }

    operator fun invoke(data: HomeAssistantConnectionConfigurationData): HttpConnectionData {
        return HttpConnectionData(
            host = data.host,
            timeout = data.timeout,
            bearerToken = data.bearerToken,
            isSSLVerificationDisabled = data.isSSLVerificationDisabled,
        )
    }

}