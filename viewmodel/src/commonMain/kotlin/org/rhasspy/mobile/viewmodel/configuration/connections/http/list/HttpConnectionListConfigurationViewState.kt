package org.rhasspy.mobile.viewmodel.configuration.connections.http.list

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.platformspecific.toStringOrEmpty
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class HttpConnectionListConfigurationViewState internal constructor(
    val items: ImmutableList<HttpConfigurationItemViewState>
) {

    @Stable
    data class HttpConfigurationItemViewState internal constructor(
        val id: Int = 5,
        val name: String = "name",
        val isRhasspy2Hermes: Boolean = false,
        val isRhasspy3Wyoming: Boolean = false,
        val isHomeAssistant: Boolean = false,
        val httpClientServerEndpointHost: String = "host",
        val httpClientServerEndpointPort: Int? = 5
    ) : IConfigurationData {

        val httpClientServerEndpointPortText: String = httpClientServerEndpointPort.toStringOrEmpty()

    }

}