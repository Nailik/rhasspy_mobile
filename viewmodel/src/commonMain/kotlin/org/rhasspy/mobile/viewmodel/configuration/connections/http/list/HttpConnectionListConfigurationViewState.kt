package org.rhasspy.mobile.viewmodel.configuration.connections.http.list

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.connection.HttpConnection
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.platformspecific.toStringOrEmpty
import org.rhasspy.mobile.platformspecific.toText
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class HttpConnectionListConfigurationViewState internal constructor(
    val items: ImmutableList<HttpConfigurationItemViewState>
) {

    @Stable
    data class HttpConfigurationItemViewState internal constructor(
        val connection: HttpConnection
    ) : IConfigurationData {

        val connectionPortText: String = connection.port.toStringOrEmpty()
        val isHermesText: StableStringResource = connection.isHermes.toText()
        val isWyomingText: StableStringResource = connection.isWyoming.toText()
        val isHomeAssistantText: StableStringResource = connection.isHomeAssistant.toText()

    }

}