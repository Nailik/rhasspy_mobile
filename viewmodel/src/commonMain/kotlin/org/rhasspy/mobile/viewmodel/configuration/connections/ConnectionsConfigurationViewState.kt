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
        val isHttpSSLVerificationEnabled: Boolean,
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

/*




            remoteHermesHttp = RemoteHermesHttpViewState(
                isHttpSSLVerificationEnabled = ConfigurationSetting.isHttpClientSSLVerificationDisabled.value,
                serviceState = ServiceViewState(httpClientService.serviceState)
            ),
            webserver = WebServerViewState(
                isHttpServerEnabled = ConfigurationSetting.isHttpServerEnabled.value,
                serviceState = ServiceViewState(webServerService.serviceState)
            ),
            mqtt = MqttViewState(
                isMQTTConnected = mqttService.isConnected.value,
                serviceState = ServiceViewState(mqttService.serviceState)
            ),
 */