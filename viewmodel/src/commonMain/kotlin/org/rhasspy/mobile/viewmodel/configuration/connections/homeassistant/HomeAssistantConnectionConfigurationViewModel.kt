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
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationUiEvent.Action.AccessTokenQRCodeClick
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.connections.homeassistant.HomeAssistantConnectionConfigurationUiEvent.Change.*

@Stable
class HomeAssistantConnectionConfigurationViewModel(
    private val mapper: HomeAssistantConnectionConfigurationDataMapper
) : ConfigurationViewModel(
    service = null
) {

    private val initialData get() = mapper(ConfigurationSetting.homeAssistantConnection.value)
    private val _viewState = MutableStateFlow(HomeAssistantConnectionConfigurationViewState(initialData))
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::initialData,
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
                    is UpdateHomeAssistantAccessToken              -> copy(bearerToken = change.text)
                }
            })
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
            AccessTokenQRCodeClick -> scanQRCode { onChange(UpdateHomeAssistantAccessToken(it)) }
        }
    }

    override fun onDiscard() {
        _viewState.update { it.copy(editData = initialData) }
    }

    override fun onSave() {
        ConfigurationSetting.homeAssistantConnection.value = mapper(_viewState.value.editData)
        _viewState.update { it.copy(editData = initialData) }
    }

}