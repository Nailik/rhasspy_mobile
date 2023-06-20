package org.rhasspy.mobile.viewmodel.configuration.edit.remotehermeshttp

import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientResult
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.recording.RecordingService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextServiceParams
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.platformspecific.toLongOrZero
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.edit.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.edit.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.edit.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.remotehermeshttp.RemoteHermesHttpConfigurationViewState.RemoteHermesHttpConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationViewState
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.RemoteHermesHttpConfigurationScreenDestination.EditScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.RemoteHermesHttpConfigurationScreenDestination.TestScreen

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