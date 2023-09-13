package org.rhasspy.mobile.viewmodel.configuration.connections

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.connections.homeassistant.IHomeAssistantConnection
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.connections.rhasspy3wyoming.IRhasspy3WyomingConnection
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationViewState.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

class ConnectionsScreenViewStateCreator(
    private val rhasspy2HermesConnection: IRhasspy2HermesConnection,
    private val rhasspy3WyomingConnection: IRhasspy3WyomingConnection,
    private val homeAssistantConnection: IHomeAssistantConnection,
    private val webServerService: IWebServerConnection,
    private val mqttService: IMqttConnection,
) {

    operator fun invoke(): StateFlow<ConnectionsConfigurationViewState> {
        return combineStateFlow(
            ConfigurationSetting.rhasspy2Connection.data,
            ConfigurationSetting.rhasspy3Connection.data,
            ConfigurationSetting.homeAssistantConnection.data,
            ConfigurationSetting.localWebserverConnection.data,
            mqttService.isConnected,
        ).mapReadonlyState {
            getViewState()
        }
    }

    private fun getViewState(): ConnectionsConfigurationViewState {
        return ConnectionsConfigurationViewState(
            rhassyp2Hermes = HttpViewState(
                host = ConfigurationSetting.rhasspy2Connection.value.host,
                serviceViewState = ServiceViewState(rhasspy2HermesConnection.connectionState)
            ),
            rhassyp3Wyoming = HttpViewState(
                host = ConfigurationSetting.rhasspy3Connection.value.host,
                serviceViewState = ServiceViewState(rhasspy3WyomingConnection.connectionState)
            ),
            homeAssistant = HttpViewState(
                host = ConfigurationSetting.homeAssistantConnection.value.host,
                serviceViewState = ServiceViewState(homeAssistantConnection.connectionState)
            ),
            webserver = WebServerViewState(
                isHttpServerEnabled = ConfigurationSetting.localWebserverConnection.value.isEnabled,
                serviceViewState = ServiceViewState(webServerService.connectionState)
            ),
            mqtt = MqttViewState(
                isMQTTConnected = mqttService.isConnected.value,
                serviceViewState = ServiceViewState(mqttService.connectionState)
            ),
        )
    }

}