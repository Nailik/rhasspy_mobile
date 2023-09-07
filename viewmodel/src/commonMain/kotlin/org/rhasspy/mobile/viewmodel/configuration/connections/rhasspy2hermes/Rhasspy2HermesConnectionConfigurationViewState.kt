package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.connection.HttpConnectionData
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState

@Stable
data class Rhasspy2HermesConnectionConfigurationViewState internal constructor(
    override val editData: HttpConnectionData
) : IConfigurationViewState