package org.rhasspy.mobile.viewmodel.configuration.connections.webserver

import org.rhasspy.mobile.data.connection.LocalWebserverConnectionData
import org.rhasspy.mobile.data.data.toIntOrZero
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationViewState.WebServerConnectionConfigurationData

class WebServerConnectionConfigurationDataMapper {

    operator fun invoke(data: LocalWebserverConnectionData): WebServerConnectionConfigurationData {
        return WebServerConnectionConfigurationData(
            isEnabled = data.isEnabled,
            port = data.port.toString(),
            isSSLEnabled = data.isSSLEnabled,
            keyStoreFile = data.keyStoreFile,
            keyStorePassword = data.keyStorePassword,
            keyAlias = data.keyAlias,
            keyPassword = data.keyPassword,
        )
    }

    operator fun invoke(data: WebServerConnectionConfigurationData): LocalWebserverConnectionData {
        return LocalWebserverConnectionData(
            isEnabled = data.isEnabled,
            port = data.port.toIntOrZero(),
            isSSLEnabled = data.isSSLEnabled,
            keyStoreFile = data.keyStoreFile,
            keyStorePassword = data.keyStorePassword,
            keyAlias = data.keyAlias,
            keyPassword = data.keyPassword,
        )
    }

}