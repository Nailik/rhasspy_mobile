package org.rhasspy.mobile.viewmodel.configuration.connections

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationViewState.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

class ConnectionsScreenViewStateCreator(
    private val userConnection: IUserConnection,
) {

    operator fun invoke(): StateFlow<ConnectionsConfigurationViewState> {
        return combineStateFlow(
            ConfigurationSetting.rhasspy2Connection.data,
            ConfigurationSetting.rhasspy3Connection.data,
            ConfigurationSetting.homeAssistantConnection.data,
            ConfigurationSetting.localWebserverConnection.data,
            ConfigurationSetting.mqttConnection.data,
        ).mapReadonlyState {
            getViewState()
        }
    }

    private fun getViewState(): ConnectionsConfigurationViewState {
        return ConnectionsConfigurationViewState(
            rhasspy2Hermes = HttpViewState(
                host = ConfigurationSetting.rhasspy2Connection.value.host,
                serviceViewState = ServiceViewState(userConnection.rhasspy2HermesHttpConnectionState)
            ),
            rhasspy3Wyoming = HttpViewState(
                host = ConfigurationSetting.rhasspy3Connection.value.host,
                serviceViewState = ServiceViewState(userConnection.rhasspy3WyomingConnectionState)
            ),
            homeAssistant = HttpViewState(
                host = ConfigurationSetting.homeAssistantConnection.value.host,
                serviceViewState = ServiceViewState(userConnection.homeAssistantConnectionState)
            ),
            webserver = WebServerViewState(
                isHttpServerEnabled = ConfigurationSetting.localWebserverConnection.value.isEnabled,
                serviceViewState = ServiceViewState(userConnection.webServerConnectionState)
            ),
            mqtt = MqttViewState(
                isMQTTEnabled = ConfigurationSetting.mqttConnection.value.isEnabled,
                serviceViewState = ServiceViewState(userConnection.rhasspy2HermesMqttConnectionState)
            ),
        )
    }

}