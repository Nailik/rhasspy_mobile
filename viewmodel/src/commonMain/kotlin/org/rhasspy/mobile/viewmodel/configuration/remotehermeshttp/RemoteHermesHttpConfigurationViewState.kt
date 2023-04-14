package org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.toLongOrZero
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState

@Stable
data class RemoteHermesHttpConfigurationViewState(
    val httpClientServerEndpointHost : String= ConfigurationSetting.httpClientServerEndpointHost.value,
    val httpClientServerEndpointPort: Int= ConfigurationSetting.httpClientServerEndpointPort.value,
    val httpClientServerEndpointPortText: String = ConfigurationSetting.httpClientServerEndpointPort.value.toString(),
    val httpClientTimeoutText: String= ConfigurationSetting.httpClientTimeout.value.toString(),
    val isHttpSSLVerificationDisabled: Boolean= ConfigurationSetting.isHttpClientSSLVerificationDisabled.value
): IConfigurationEditViewState() {

    override val hasUnsavedChanges: Boolean
        get() = !(httpClientServerEndpointHost == ConfigurationSetting.httpClientServerEndpointHost.value &&
                httpClientServerEndpointPort == ConfigurationSetting.httpClientServerEndpointPort.value &&
                httpClientTimeout == ConfigurationSetting.httpClientTimeout.value &&
                isHttpSSLVerificationDisabled == ConfigurationSetting.isHttpClientSSLVerificationDisabled.value)

    override val isTestingEnabled: Boolean
        get() = httpClientServerEndpointHost.isNotBlank() &&
                (ConfigurationSetting.speechToTextOption.value == SpeechToTextOption.RemoteHTTP ||
                        ConfigurationSetting.intentRecognitionOption.value == IntentRecognitionOption.RemoteHTTP ||
                        ConfigurationSetting.textToSpeechOption.value == TextToSpeechOption.RemoteHTTP)

    val httpClientTimeout: Long get() = httpClientTimeoutText.toLongOrZero()

}