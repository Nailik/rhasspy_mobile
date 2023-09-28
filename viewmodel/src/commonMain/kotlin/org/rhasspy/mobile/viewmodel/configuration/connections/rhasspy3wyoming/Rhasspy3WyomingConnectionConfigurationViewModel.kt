package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.data.toLongOrNullOrConstant
import org.rhasspy.mobile.logic.connections.rhasspy3wyoming.IRhasspy3WyomingConnection
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationUiEvent.Action.AccessTokenQRCodeClick
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationUiEvent.Change.*

@Stable
class Rhasspy3WyomingConnectionConfigurationViewModel(
    private val mapper: Rhasspy3WyomingConnectionConfigurationDataMapper,
    rhasspy3WyomingConnection: IRhasspy3WyomingConnection
) : ConfigurationViewModel(
    connectionState = rhasspy3WyomingConnection.connectionState
) {

    private val initialData get() = mapper(ConfigurationSetting.rhasspy3Connection.value)
    private val _viewState = MutableStateFlow(Rhasspy3WyomingConnectionConfigurationViewState(initialData))
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
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick              -> navigator.onBackPressed()
            AccessTokenQRCodeClick -> scanQRCode { onChange(UpdateRhasspy3WyomingAccessToken(it)) }
        }
    }

    override fun onDiscard() {
        _viewState.update { it.copy(editData = initialData) }
    }

    override fun onSave() {
        ConfigurationSetting.rhasspy3Connection.value = mapper(_viewState.value.editData)
        _viewState.update { it.copy(editData = initialData) }
    }

}