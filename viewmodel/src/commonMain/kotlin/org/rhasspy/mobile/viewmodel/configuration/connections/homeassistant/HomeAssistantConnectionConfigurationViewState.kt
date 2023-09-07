package org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.connection.HttpConnectionData
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState

@Stable
data class HomeAssistantConnectionConfigurationViewState internal constructor(
    override val editData: HttpConnectionData
) : IConfigurationViewState 

    