package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.data.toLongOrNullOrConstant
import org.rhasspy.mobile.logic.connections.rhasspy3wyoming.IRhasspy3WyomingConnection
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationUiEvent.Action.AccessTokenQRCodeClick
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class Rhasspy3WyomingConnectionConfigurationViewModel(
    private val mapper: Rhasspy3WyomingConnectionConfigurationDataMapper,
    rhasspy3WyomingConnection: IRhasspy3WyomingConnection
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(
        Rhasspy3WyomingConnectionConfigurationViewState(
            editData = mapper(ConfigurationSetting.rhasspy3Connection.value),
            connectionState = rhasspy3WyomingConnection.connectionState
        )
    )
    val viewState = _viewState.readOnly

    fun onEvent(event: Rhasspy3WyomingConnectionConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SetRhasspy3WyomingSSLVerificationDisabled -> copy(isSSLVerificationDisabled = change.disabled)
                    is UpdateRhasspy3WyomingServerEndpointHost   -> copy(host = change.host)
                    is UpdateRhasspy3WyomingTimeout              -> copy(timeout = change.text.toLongOrNullOrConstant())
                    is UpdateRhasspy3WyomingAccessToken          -> copy(bearerToken = change.text)
                }
            })
        }
        ConfigurationSetting.rhasspy3Connection.value = mapper(_viewState.value.editData)
    }

    private fun onAction(action: Action) {
        when (action) {
            AccessTokenQRCodeClick -> scanQRCode { onChange(UpdateRhasspy3WyomingAccessToken(it)) }
        }
    }

}