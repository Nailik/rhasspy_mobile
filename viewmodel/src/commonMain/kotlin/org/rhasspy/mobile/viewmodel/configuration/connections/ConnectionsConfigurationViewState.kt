package org.rhasspy.mobile.viewmodel.configuration.connections

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

data class ConnectionsConfigurationViewState internal constructor(
    val rhasspy2Hermes: HttpViewState,
    val rhasspy3Wyoming: HttpViewState,
    val homeAssistant: HttpViewState,
    val webserver: WebServerViewState,
    val mqtt: MqttViewState,
) {

    @Stable
    data class HttpViewState internal constructor(
        val host: String,
        val serviceViewState: ServiceViewState
    )

    @Stable
    data class MqttViewState internal constructor(
        val isMQTTConnected: Boolean,
        val serviceViewState: ServiceViewState
    )

    @Stable
    data class WebServerViewState internal constructor(
        val isHttpServerEnabled: Boolean,
        val serviceViewState: ServiceViewState
    )

}