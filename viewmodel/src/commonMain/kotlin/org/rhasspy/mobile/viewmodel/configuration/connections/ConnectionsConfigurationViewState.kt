package org.rhasspy.mobile.viewmodel.configuration.connections

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

data class ConnectionsConfigurationViewState internal constructor(
    val http: HttpViewState,
    val webserver: WebServerViewState,
    val mqtt: MqttViewState,
) {

    @Stable
    data class HttpViewState internal constructor(
        val httpConnectionCount: Int,
        val serviceState: ServiceViewState
    )

    @Stable
    data class MqttViewState internal constructor(
        val isMQTTConnected: Boolean,
        val serviceState: ServiceViewState
    )

    @Stable
    data class WebServerViewState internal constructor(
        val isHttpServerEnabled: Boolean,
        val serviceState: ServiceViewState
    )

}