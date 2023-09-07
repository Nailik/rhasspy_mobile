package org.rhasspy.mobile.viewmodel.configuration.connections.mqtt

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.connection.MqttConnectionData
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState

@Stable
data class MqttConnectionConfigurationViewState internal constructor(
    override val editData: MqttConnectionData
) : IConfigurationViewState