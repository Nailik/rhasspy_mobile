package org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState

@Stable
data class RemoteHermesHttpConfigurationViewState internal constructor(
    val httpClientServerEndpointHost: String = ConfigurationSetting.httpClientServerEndpointHost.value,
    val httpClientServerEndpointPort: Int = ConfigurationSetting.httpClientServerEndpointPort.value,
    val httpClientServerEndpointPortText: String = ConfigurationSetting.httpClientServerEndpointPort.value.toString(),
    val httpClientTimeoutText: String = ConfigurationSetting.httpClientTimeout.value.toString(),
    val isHttpSSLVerificationDisabled: Boolean = ConfigurationSetting.isHttpClientSSLVerificationDisabled.value,
    val testIntentRecognitionText: String = "",
    val testTextToSpeechText: String = "",
    val isTestRecordingAudio: Boolean = false
) : IConfigurationEditViewState() {

    val isSpeechToTextTestVisible
        get() =
            ConfigurationSetting.speechToTextOption.value == SpeechToTextOption.RemoteHTTP &&
                    !ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value

    val isIntentRecognitionTestVisible
        get() =
            ConfigurationSetting.intentRecognitionOption.value == IntentRecognitionOption.RemoteHTTP
                    && !ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value

    val isTextToSpeechTestVisible
        get() =
            ConfigurationSetting.textToSpeechOption.value == TextToSpeechOption.RemoteHTTP
                    && !ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value

    override val isTestingEnabled: Boolean
        get() = httpClientServerEndpointHost.isNotBlank() &&
                (ConfigurationSetting.speechToTextOption.value == SpeechToTextOption.RemoteHTTP ||
                        ConfigurationSetting.intentRecognitionOption.value == IntentRecognitionOption.RemoteHTTP ||
                        ConfigurationSetting.textToSpeechOption.value == TextToSpeechOption.RemoteHTTP)

}