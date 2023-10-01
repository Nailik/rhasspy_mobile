package org.rhasspy.mobile.viewmodel.configuration.connections.mqtt

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.data.toStringOrEmpty
import org.rhasspy.mobile.data.service.ConnectionState

@Stable
data class MqttConnectionConfigurationViewState internal constructor(
    val editData: MqttConnectionConfigurationData,
    val connectionState: StateFlow<ConnectionState>,
) {

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
    ) {

        val connectionTimeoutText: String = connectionTimeout.toStringOrEmpty()
        val keepAliveIntervalText: String = keepAliveInterval.toStringOrEmpty()
        val retryIntervalText: String = retryInterval.toStringOrEmpty()

    }

}