package org.rhasspy.mobile.viewmodel.configuration.connections

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.connections.httpclient.IHttpClientService
import org.rhasspy.mobile.logic.connections.mqtt.IMqttService
import org.rhasspy.mobile.logic.connections.webserver.IWebServerService
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationViewState.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

class ConnectionsScreenViewStateCreator(
    private val httpClientService: IHttpClientService,
    private val webServerService: IWebServerService,
    private val mqttService: IMqttService,
) {

    operator fun invoke(): StateFlow<ConnectionsConfigurationViewState> {
        return combineStateFlow(
            httpClientService.serviceState,
            webServerService.serviceState,
            mqttService.serviceState,
            mqttService.isConnected,
            ConfigurationSetting.isHttpServerEnabled.data,
        ).mapReadonlyState {
            getViewState()
        }
    }

    private fun getViewState(): ConnectionsConfigurationViewState {
        return ConnectionsConfigurationViewState(
            http = HttpViewState(
                httpConnectionCount = 2, //TODO
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
        )
    }

}