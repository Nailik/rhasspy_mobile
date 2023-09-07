package org.rhasspy.mobile.viewmodel.configuration.connections

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationViewState.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

class ConnectionsScreenViewStateCreator(
    private val webServerService: IWebServerConnection,
    private val mqttService: IMqttConnection,
) {

    operator fun invoke(): StateFlow<ConnectionsConfigurationViewState> {
        return combineStateFlow(
            webServerService.serviceState,
            mqttService.serviceState,
            mqttService.isConnected,
            ConfigurationSetting.localWebserverConnection.data,
        ).mapReadonlyState {
            getViewState()
        }
    }

    private fun getViewState(): ConnectionsConfigurationViewState {
        return ConnectionsConfigurationViewState(
            rhassyp2Hermes = HttpViewState(
                host = "rhassyp2Hermes",
            ),
            rhassyp3Wyoming = HttpViewState(
                host = "rhassyp3Wyoming",
            ),
            homeAssistant = HttpViewState(
                host = "homeAssistant",
            ),
            webserver = WebServerViewState(
                isHttpServerEnabled = ConfigurationSetting.localWebserverConnection.value.isEnabled,
                serviceState = ServiceViewState(webServerService.serviceState)
            ),
            mqtt = MqttViewState(
                isMQTTConnected = mqttService.isConnected.value,
                serviceState = ServiceViewState(mqttService.serviceState)
            ),
        )
    }

}