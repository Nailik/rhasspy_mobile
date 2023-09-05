package org.rhasspy.mobile.viewmodel.configuration.connections.http.detail

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.connection.HttpConnectionParams
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.toLongOrNullOrConstant
import org.rhasspy.mobile.settings.repositories.IHttpConnectionSettingRepository
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationViewState.HttpConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConnectionScreenNavigationDestination.HttpConnectionDetailScreen

@Stable
class HttpConnectionDetailConfigurationViewModel(
    destination: HttpConnectionDetailScreen,
    private val httpConnectionSettingRepository: IHttpConnectionSettingRepository
) : ConfigurationViewModel(
    service = null
) {

    private var connection: HttpConnectionParams? = destination.id

    private val _viewState = MutableStateFlow(HttpConnectionDetailConfigurationViewState(HttpConfigurationData(connection)))
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = { HttpConfigurationData(connection) },
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
                    is SetHttpSSLVerificationDisabled     -> copy(isSSLVerificationDisabled = change.disabled)
                    is UpdateHttpClientServerEndpointHost -> copy(host = change.host)
                    is UpdateHttpClientTimeout            -> copy(timeout = change.text.toLongOrNullOrConstant())
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
        _viewState.update { it.copy(editData = HttpConfigurationData(connection)) }
    }

    override fun onSave() {
        with(_viewState.value.editData) {
            connection = HttpConnectionParams(
                id = connection?.id,
                host = host,
                timeout = timeout,
                bearerToken = bearerToken,
                isSSLVerificationDisabled = isSSLVerificationDisabled,
            ).also {
                httpConnectionSettingRepository.addOrUpdateHttpConnection(it)
            }
        }
    }

}