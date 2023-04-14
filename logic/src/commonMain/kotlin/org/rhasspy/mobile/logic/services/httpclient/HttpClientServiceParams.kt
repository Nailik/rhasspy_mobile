package org.rhasspy.mobile.logic.services.httpclient

import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting

data class HttpClientServiceParams(
    val siteId: String = ConfigurationSetting.siteId.value,
    val isHttpSSLVerificationDisabled: Boolean = ConfigurationSetting.isHttpClientSSLVerificationDisabled.value,
    val httpClientServerEndpointHost: String = ConfigurationSetting.httpClientServerEndpointHost.value,
    val httpClientServerEndpointPort: Int = ConfigurationSetting.httpClientServerEndpointPort.value,
    val httpClientTimeout: Long? = ConfigurationSetting.httpClientTimeout.value,
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