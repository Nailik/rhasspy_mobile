package org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.data.toLongOrNullOrConstant
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.connections.rhasspy3wyoming.Rhasspy3WyomingConnectionConfigurationUiEvent.Change.*

@Stable
class Rhasspy3WyomingConnectionConfigurationViewModel : ConfigurationViewModel(
    service = null
) {

    private val _viewState = MutableStateFlow(Rhasspy3WyomingConnectionConfigurationViewState(ConfigurationSetting.rhasspy3Connection.value))
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = { ConfigurationSetting.rhasspy3Connection.value },
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
        _viewState.update { it.copy(editData = ConfigurationSetting.rhasspy3Connection.value) }
    }

    override fun onSave() {
        ConfigurationSetting.rhasspy3Connection.value = _viewState.value.editData
    }

}