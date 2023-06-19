package org.rhasspy.mobile.viewmodel.configuration.edit.remotehermeshttp

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.settings.ConfigurationSetting

@Stable
data class RemoteHermesHttpConfigurationViewState internal constructor(
    val editData: RemoteHermesHttpConfigurationData
) {

    @Stable
    data class RemoteHermesHttpConfigurationData internal constructor(
        val httpClientServerEndpointHost: String = ConfigurationSetting.httpClientServerEndpointHost.value,
        val httpClientServerEndpointPort: Int? = ConfigurationSetting.httpClientServerEndpointPort.value,
        val httpClientTimeout: Long = ConfigurationSetting.httpClientTimeout.value,
        val isHttpSSLVerificationDisabled: Boolean = ConfigurationSetting.isHttpClientSSLVerificationDisabled.value
    ) {

        val httpClientServerEndpointPortText: String = httpClientServerEndpointPort.toString()
        val httpClientTimeoutText: String = httpClientTimeout.toString()

    }

}

// override val isTestingEnabled: Boolean
//        get() = httpClientServerEndpointHost.isNotBlank() &&
//                (ConfigurationSetting.speechToTextOption.value == SpeechToTextOption.RemoteHTTP ||
//                        ConfigurationSetting.intentRecognitionOption.value == IntentRecognitionOption.RemoteHTTP ||
//                        ConfigurationSetting.textToSpeechOption.value == TextToSpeechOption.RemoteHTTP)
// val testIntentRecognitionText: String = "",
// val testTextToSpeechText: String = "",
// val isTestRecordingAudio: Boolean = false

/*


    val isSpeechToTextTestVisible  = ConfigurationSetting.speechToTextOption.value == SpeechToTextOption.RemoteHTTP &&
                    !ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value

    val isIntentRecognitionTestVisible = ConfigurationSetting.intentRecognitionOption.value == IntentRecognitionOption.RemoteHTTP
                    && !ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value

    val isTextToSpeechTestVisible = ConfigurationSetting.textToSpeechOption.value == TextToSpeechOption.RemoteHTTP
                    && !ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value
 */