package org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.ServiceStateHeaderViewState

@Stable
data class RemoteHermesHttpConfigurationViewState(
    val httpClientServerEndpointHost : String,
    val httpClientServerEndpointPort: Int,
    val httpClientServerEndpointPortText: String,
    val httpClientTimeout: Long?,
    val httpClientTimeoutText: String,
    val isHttpSSLVerificationDisabled: Boolean
): IConfigurationContentViewState() {

    companion object {
        fun getInitial() = RemoteHermesHttpConfigurationViewState(
            httpClientServerEndpointHost = ConfigurationSetting.httpClientServerEndpointHost.value,
            httpClientServerEndpointPort = ConfigurationSetting.httpClientServerEndpointPort.value,
            httpClientServerEndpointPortText = ConfigurationSetting.httpClientServerEndpointPort.value.toString(),
            httpClientTimeout = ConfigurationSetting.httpClientTimeout.value,
            httpClientTimeoutText = ConfigurationSetting.httpClientTimeout.value.toString(),
            isHttpSSLVerificationDisabled = ConfigurationSetting.isHttpClientSSLVerificationDisabled.value
        )
    }

    override fun getEditViewState(serviceViewState: StateFlow<ServiceStateHeaderViewState>): IConfigurationEditViewState {
        return IConfigurationEditViewState(
            hasUnsavedChanges = !(httpClientServerEndpointHost == ConfigurationSetting.httpClientServerEndpointHost.value &&
                    httpClientServerEndpointPort == ConfigurationSetting.httpClientServerEndpointPort.value &&
                    httpClientTimeout == ConfigurationSetting.httpClientTimeout.value &&
                    isHttpSSLVerificationDisabled == ConfigurationSetting.isHttpClientSSLVerificationDisabled.value),
            isTestingEnabled = httpClientServerEndpointHost.isNotBlank() &&
                    (ConfigurationSetting.speechToTextOption.value == SpeechToTextOption.RemoteHTTP ||
                            ConfigurationSetting.intentRecognitionOption.value == IntentRecognitionOption.RemoteHTTP ||
                            ConfigurationSetting.textToSpeechOption.value == TextToSpeechOption.RemoteHTTP),
            serviceViewState = serviceViewState
        )
    }

    override fun save() {
        ConfigurationSetting.httpClientServerEndpointHost.value = httpClientServerEndpointHost
        ConfigurationSetting.httpClientServerEndpointPort.value = httpClientServerEndpointPort
        ConfigurationSetting.isHttpClientSSLVerificationDisabled.value = isHttpSSLVerificationDisabled
    }

}