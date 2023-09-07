package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.connection.HttpConnectionData
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState

@Stable
data class Rhasspy3WyomingConnectionConfigurationViewState internal constructor(
    override val editData: HttpConnectionData
) : IConfigurationViewState