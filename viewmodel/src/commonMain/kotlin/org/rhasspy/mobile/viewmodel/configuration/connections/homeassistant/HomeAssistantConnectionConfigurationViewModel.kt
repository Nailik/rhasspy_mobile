package org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.data.toLongOrNullOrConstant
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationUiEvent.Change.*

@Stable
class HomeAssistantConnectionConfigurationViewModel : ConfigurationViewModel(
    service = null
) {

    private val _viewState = MutableStateFlow(HomeAssistantConnectionConfigurationViewState(ConfigurationSetting.homeAssistantConnection.value))
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = { ConfigurationSetting.homeAssistantConnection.value },
            viewState = viewState,
            configurationViewState = configurationViewState
        )
    }

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
                    is UpdateHomeAssistantClientTimeout            -> copy(timeout = change.text.toLongOrNullOrConstant())
                }
            })
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
        }
    }

    override fun onDiscard() {
        _viewState.update { it.copy(editData = ConfigurationSetting.homeAssistantConnection.value) }
    }

    override fun onSave() {
        ConfigurationSetting.homeAssistantConnection.value = _viewState.value.editData
    }

}