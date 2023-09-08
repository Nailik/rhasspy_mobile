package org.rhasspy.mobile.viewmodel.configuration.connections.mqtt

import org.rhasspy.mobile.data.connection.MqttConnectionData
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationViewState.MqttConnectionConfigurationData

class MqttConnectionConfigurationDataMapper {

    operator fun invoke(data: MqttConnectionData): MqttConnectionConfigurationData {
        return MqttConnectionConfigurationData(
            isEnabled = data.isEnabled,
            host = data.host,
            userName = data.userName,
            password = data.password,
            isSSLEnabled = data.isSslEnabled,
            connectionTimeout = data.connectionTimeout,
            keepAliveInterval = data.keepAliveInterval,
            retryInterval = data.retryInterval,
            keystoreFile = data.keystoreFile
        )
    }

    operator fun invoke(data: MqttConnectionConfigurationData): MqttConnectionData {
        return MqttConnectionData(
            isEnabled = data.isEnabled,
            host = data.host,
            userName = data.userName,
            password = data.password,
            isSslEnabled = data.isSSLEnabled,
            connectionTimeout = data.connectionTimeout ?: 0,
            keepAliveInterval = data.keepAliveInterval ?: 0,
            retryInterval = data.retryInterval ?: 0L,
            keystoreFile = data.keystoreFile
        )
    }

}