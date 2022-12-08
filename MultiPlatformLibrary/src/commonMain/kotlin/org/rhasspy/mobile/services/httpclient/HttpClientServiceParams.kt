package org.rhasspy.mobile.services.httpclient

import org.rhasspy.mobile.data.IntentHandlingOptions
import org.rhasspy.mobile.settings.ConfigurationSettings

data class HttpClientServiceParams(
    val siteId: String = ConfigurationSettings.siteId.value,
    val isHttpSSLVerificationDisabled: Boolean = ConfigurationSettings.isHttpServerSSLEnabled.value,
    val httpServerEndpointHost: String = ConfigurationSettings.httpServerEndpointHost.value,
    val httpServerEndpointPort: Int = ConfigurationSettings.httpServerEndpointPort.value,
    val isUseCustomSpeechToTextHttpEndpoint: Boolean = ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value,
    val speechToTextHttpEndpoint: String = ConfigurationSettings.speechToTextHttpEndpoint.value,
    val isUseCustomIntentRecognitionHttpEndpoint: Boolean = ConfigurationSettings.isUseCustomIntentRecognitionHttpEndpoint.value,
    val intentRecognitionHttpEndpoint: String = ConfigurationSettings.intentRecognitionHttpEndpoint.value,
    val isUseCustomTextToSpeechHttpEndpoint: Boolean = ConfigurationSettings.isUseCustomTextToSpeechHttpEndpoint.value,
    val textToSpeechHttpEndpoint: String = ConfigurationSettings.textToSpeechHttpEndpoint.value,
    val isUseCustomAudioPlayingEndpoint: Boolean = ConfigurationSettings.isUseCustomAudioPlayingHttpEndpoint.value,
    val audioPlayingHttpEndpoint: String = ConfigurationSettings.audioPlayingHttpEndpoint.value,
    val intentHandlingHttpEndpoint: String = ConfigurationSettings.intentHandlingHttpEndpoint.value,
    val intentHandlingHassEndpoint: String = ConfigurationSettings.intentHandlingHassEndpoint.value,
    val intentHandlingHassAccessToken: String = ConfigurationSettings.intentHandlingHassAccessToken.value,
    val intentHandlingOption: IntentHandlingOptions = ConfigurationSettings.intentHandlingOption.value
)