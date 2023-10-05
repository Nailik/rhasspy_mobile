package org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.data.takeInt
import org.rhasspy.mobile.logic.connections.homeassistant.IHomeAssistantConnection
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationUiEvent.Action.AccessTokenQRCodeClick
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class HomeAssistantConnectionConfigurationViewModel(
    private val mapper: HomeAssistantConnectionConfigurationDataMapper,
    homeAssistantConnection: IHomeAssistantConnection,
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(
        HomeAssistantConnectionConfigurationViewState(
            editData = mapper(ConfigurationSetting.homeAssistantConnection.value),
            connectionState = homeAssistantConnection.connectionState,
        )
    )
    val viewState = _viewState.readOnly

    fun onEvent(event: HomeAssistantConnectionConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SetHomeAssistantSSLVerificationDisabled     -> copy(isSSLVerificationDisabled = change.disabled)
                    is UpdateHomeAssistantClientServerEndpointHost -> copy(host = change.host)
                    is UpdateHomeAssistantClientTimeout            -> copy(timeout = change.text.takeInt())
                    is UpdateHomeAssistantAccessToken              -> copy(bearerToken = change.text)
                }
            })
        }
        ConfigurationSetting.homeAssistantConnection.value = mapper(_viewState.value.editData)
    }

    private fun onAction(action: Action) {
        when (action) {
            AccessTokenQRCodeClick -> scanQRCode { onChange(UpdateHomeAssistantAccessToken(it)) }
        }
    }

}