package org.rhasspy.mobile.viewmodel.configuration.connections.mqtt

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.data.toStringOrEmpty
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState.IConfigurationData

@Stable
data class MqttConnectionConfigurationViewState internal constructor(
    override val editData: MqttConnectionConfigurationData
) : IConfigurationViewState {

    @Stable
    data class MqttConnectionConfigurationData internal constructor(
        val isEnabled: Boolean,
        val host: String,
        val userName: String,
        val password: String,
        val isSSLEnabled: Boolean,
        val connectionTimeout: Int?,
        val keepAliveInterval: Int?,
        val retryInterval: Long?,
        val keystoreFile: String?
    ) : IConfigurationData {

        val connectionTimeoutText: String = connectionTimeout.toStringOrEmpty()
        val keepAliveIntervalText: String = keepAliveInterval.toStringOrEmpty()
        val retryIntervalText: String = retryInterval.toStringOrEmpty()

    }

}