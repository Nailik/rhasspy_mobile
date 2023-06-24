package org.rhasspy.mobile.viewmodel.configuration.edit.remotehermeshttp

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.platformspecific.toLongOrZero
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.edit.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.edit.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.edit.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.remotehermeshttp.RemoteHermesHttpConfigurationViewState.RemoteHermesHttpConfigurationData

@Stable
class RemoteHermesHttpConfigurationEditViewModel(
    service: HttpClientService,
    private val viewStateCreator: ConfigurationEditViewStateCreator
) : IConfigurationEditViewModel(
    service = service
) {

    private val initialConfigurationData = RemoteHermesHttpConfigurationData()

    private val _editData = MutableStateFlow(initialConfigurationData)
    private val _viewState = MutableStateFlow(RemoteHermesHttpConfigurationViewState(initialConfigurationData))
    val viewState = combineState(_viewState, _editData) { viewState, editData ->
        viewState.copy(editData = editData)
    }

    override fun initViewStateCreator(
        configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>
    ): StateFlow<ConfigurationEditViewState> {
        return viewStateCreator(
            init = ::RemoteHermesHttpConfigurationData,
            editData = _editData,
            configurationEditViewState = configurationEditViewState
        )
    }

    fun onEvent(event: RemoteHermesHttpConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        with(_editData.value) {
            when (change) {
                is SetHttpSSLVerificationDisabled -> copy(isHttpSSLVerificationDisabled = change.disabled)
                is UpdateHttpClientServerEndpointHost -> copy(httpClientServerEndpointHost = change.host)
                is UpdateHttpClientServerEndpointPort -> copy(httpClientServerEndpointPort = change.port.toIntOrNull())
                is UpdateHttpClientTimeout -> copy(httpClientTimeout = change.text.toLongOrNull())
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
        }
    }

    override fun onDiscard() {}

    override fun onSave() {
        with(_editData.value) {
            ConfigurationSetting.httpClientServerEndpointHost.value = httpClientServerEndpointHost
            ConfigurationSetting.httpClientServerEndpointPort.value = httpClientServerEndpointPort.toIntOrZero()
            ConfigurationSetting.isHttpClientSSLVerificationDisabled.value = isHttpSSLVerificationDisabled
            ConfigurationSetting.httpClientTimeout.value = httpClientTimeout.toLongOrZero()
        }
    }

}