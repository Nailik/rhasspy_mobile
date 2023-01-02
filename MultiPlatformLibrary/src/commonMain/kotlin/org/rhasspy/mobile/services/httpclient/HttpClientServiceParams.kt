package org.rhasspy.mobile.services.httpclient

import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.IntentHandlingOption

//TODO custom timeout for every service?
data class HttpClientServiceParams(
    val siteId: String = ConfigurationSetting.siteId.value,
    val isHttpSSLVerificationDisabled: Boolean = ConfigurationSetting.isHttpClientSSLVerificationDisabled.value,
    val httpServerEndpointHost: String = ConfigurationSetting.httpClientServerEndpointHost.value,
    val httpServerEndpointPort: Int = ConfigurationSetting.httpClientServerEndpointPort.value,
    val isUseCustomSpeechToTextHttpEndpoint: Boolean = ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value,
    val speechToTextHttpEndpoint: String = ConfigurationSetting.speechToTextHttpEndpoint.value,
    val isUseCustomIntentRecognitionHttpEndpoint: Boolean = ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value,
    val intentRecognitionHttpEndpoint: String = ConfigurationSetting.intentRecognitionHttpEndpoint.value,
    val isUseCustomTextToSpeechHttpEndpoint: Boolean = ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value,
    val textToSpeechHttpEndpoint: String = ConfigurationSetting.textToSpeechHttpEndpoint.value,
    val isUseCustomAudioPlayingEndpoint: Boolean = ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value,
    val audioPlayingHttpEndpoint: String = ConfigurationSetting.audioPlayingHttpEndpoint.value,
    val intentHandlingHttpEndpoint: String = ConfigurationSetting.intentHandlingHttpEndpoint.value,
    val intentHandlingHassEndpoint: String = ConfigurationSetting.intentHandlingHassEndpoint.value,
    val intentHandlingHassAccessToken: String = ConfigurationSetting.intentHandlingHassAccessToken.value,
    val intentHandlingOption: IntentHandlingOption = ConfigurationSetting.intentHandlingOption.value
)