package org.rhasspy.mobile.viewmodel.configuration.connections.mqtt

import org.rhasspy.mobile.data.connection.MqttConnectionData
import org.rhasspy.mobile.data.data.toIntOrZero
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationViewState.MqttConnectionConfigurationData
import kotlin.time.Duration.Companion.seconds

class MqttConnectionConfigurationDataMapper {

    operator fun invoke(data: MqttConnectionData): MqttConnectionConfigurationData {
        return MqttConnectionConfigurationData(
            isEnabled = data.isEnabled,
            host = data.host,
            userName = data.userName,
            password = data.password,
            isSSLEnabled = data.isSSLEnabled,
            connectionTimeout = data.connectionTimeout.inWholeSeconds.toString(),
            keepAliveInterval = data.keepAliveInterval.inWholeSeconds.toString(),
            retryInterval = data.retryInterval.inWholeSeconds.toString(),
            keystoreFile = data.keystoreFile,
        )
    }

    operator fun invoke(data: MqttConnectionConfigurationData): MqttConnectionData {
        return MqttConnectionData(
            isEnabled = data.isEnabled,
            host = data.host,
            userName = data.userName,
            password = data.password,
            isSSLEnabled = data.isSSLEnabled,
            connectionTimeout = data.connectionTimeout.toIntOrZero().seconds,
            keepAliveInterval = data.keepAliveInterval.toIntOrZero().seconds,
            retryInterval = data.retryInterval.toIntOrZero().seconds,
            keystoreFile = data.keystoreFile,
        )
    }

}