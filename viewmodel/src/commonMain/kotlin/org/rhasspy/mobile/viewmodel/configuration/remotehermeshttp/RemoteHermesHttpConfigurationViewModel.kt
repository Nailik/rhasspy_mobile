package org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.services.httpclient.IHttpClientService
import org.rhasspy.mobile.platformspecific.*
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationViewState.RemoteHermesHttpConfigurationData

@Stable
class RemoteHermesHttpConfigurationViewModel(
    service: IHttpClientService
) : IConfigurationViewModel(
    service = service
) {

    private val _viewState = MutableStateFlow(RemoteHermesHttpConfigurationViewState(RemoteHermesHttpConfigurationData()))
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::RemoteHermesHttpConfigurationData,
            viewState = viewState,
            configurationViewState = configurationViewState
        )
    }

    fun onEvent(event: RemoteHermesHttpConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SetHttpSSLVerificationDisabled -> copy(isHttpSSLVerificationDisabled = change.disabled)
                    is UpdateHttpClientServerEndpointHost -> copy(httpClientServerEndpointHost = change.host)
                    is UpdateHttpClientServerEndpointPort -> copy(httpClientServerEndpointPort = change.port.toIntOrNullOrConstant())
                    is UpdateHttpClientTimeout -> copy(httpClientTimeout = change.text.toLongOrNullOrConstant())
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
        _viewState.update { it.copy(editData = RemoteHermesHttpConfigurationData()) }
    }

    override fun onSave() {
        with(_viewState.value.editData) {
            ConfigurationSetting.httpClientServerEndpointHost.value = httpClientServerEndpointHost
            ConfigurationSetting.httpClientServerEndpointPort.value = httpClientServerEndpointPort.toIntOrZero()
            ConfigurationSetting.isHttpClientSSLVerificationDisabled.value = isHttpSSLVerificationDisabled
            ConfigurationSetting.httpClientTimeout.value = httpClientTimeout.toLongOrZero()
        }
    }

}