package org.rhasspy.mobile.viewmodel.configuration.connections.http.list

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.connection.HttpConnectionParams
import org.rhasspy.mobile.platformspecific.toStringOrEmpty
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class HttpConnectionListConfigurationViewState internal constructor(
    val items: ImmutableList<HttpConfigurationItemViewState>
) {

    @Stable
    data class HttpConfigurationItemViewState internal constructor(
        val connection: HttpConnectionParams
    ) : IConfigurationData {

        val connectionPortText: String = connection.port.toStringOrEmpty()

    }

}