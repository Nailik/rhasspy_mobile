package org.rhasspy.mobile.viewmodel.configuration.connections.http.detail

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.connections.httpclient.IHttpClientService
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.toIntOrNullOrConstant
import org.rhasspy.mobile.platformspecific.toLongOrNullOrConstant
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationViewState.RemoteHermesHttpConfigurationData

@Stable
class HttpConnectionDetailConfigurationViewModel(
    private val id: Int?,
    service: IHttpClientService
) : ConfigurationViewModel(
    service = service
) {

    private val _viewState = MutableStateFlow(HttpConnectionDetailConfigurationViewState(RemoteHermesHttpConfigurationData()))
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

    fun onEvent(event: HttpConnectionDetailConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SetHttpSSLVerificationDisabled     -> copy(isHttpSSLVerificationDisabled = change.disabled)
                    is UpdateHttpClientServerEndpointHost -> copy(httpClientServerEndpointHost = change.host)
                    is UpdateHttpClientServerEndpointPort -> copy(httpClientServerEndpointPort = change.port.toIntOrNullOrConstant())
                    is UpdateHttpClientTimeout            -> copy(httpClientTimeout = change.text.toLongOrNullOrConstant())
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
            //TODO ConfigurationSetting.httpClientServerEndpointHost.value = httpClientServerEndpointHost
            //TODO ConfigurationSetting.httpClientServerEndpointPort.value = httpClientServerEndpointPort.toIntOrZero()
            //TODO ConfigurationSetting.isHttpClientSSLVerificationDisabled.value = isHttpSSLVerificationDisabled
            //TODO ConfigurationSetting.httpClientTimeout.value = httpClientTimeout.toLongOrZero()
        }
    }

}