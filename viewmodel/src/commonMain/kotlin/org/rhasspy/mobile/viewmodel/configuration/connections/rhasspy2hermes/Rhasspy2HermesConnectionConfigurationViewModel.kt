package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.data.toLongOrNullOrConstant
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationUiEvent.Action.AccessTokenQRCodeClick
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy2hermes.Rhasspy2HermesConnectionConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class Rhasspy2HermesConnectionConfigurationViewModel(
    private val mapper: Rhasspy2HermesConnectionConfigurationDataMapper,
    rhasspy2HermesConnection: IRhasspy2HermesConnection
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(
        Rhasspy2HermesConnectionConfigurationViewState(
            editData = mapper(ConfigurationSetting.rhasspy2Connection.value),
            connectionState = rhasspy2HermesConnection.connectionState
        )
    )
    val viewState = _viewState.readOnly

    fun onEvent(event: Rhasspy2HermesConnectionConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SetRhasspy2HermesSSLVerificationDisabled -> copy(isSSLVerificationDisabled = change.disabled)
                    is UpdateRhasspy2HermesServerEndpointHost   -> copy(host = change.host)
                    is UpdateRhasspy2HermesTimeout              -> copy(timeout = change.text.toLongOrNullOrConstant())
                    is UpdateRhasspy2HermesAccessToken          -> copy(bearerToken = change.text)
                }
            })
        }
        ConfigurationSetting.rhasspy2Connection.value = mapper(_viewState.value.editData)
    }

    private fun onAction(action: Action) {
        when (action) {
            AccessTokenQRCodeClick -> scanQRCode { onChange(UpdateRhasspy2HermesAccessToken(it)) }
        }
    }

}