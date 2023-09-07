package org.rhasspy.mobile.viewmodel.configuration.connections.webserver

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.connection.LocalWebserverConnectionData
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState

@Stable
data class WebServerConnectionConfigurationViewState internal constructor(
    override val editData: LocalWebserverConnectionData
) : IConfigurationViewState
