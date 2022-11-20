package org.rhasspy.mobile.services.httpclient

import org.rhasspy.mobile.data.IntentHandlingOptions
import org.rhasspy.mobile.settings.ConfigurationSettings

data class HttpClientParams(
    val isHttpSSLVerificationDisabled: Boolean = ConfigurationSettings.isHttpServerSSLEnabled.value,
    val speechToTextHttpEndpoint: String = if (ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value) {
        ConfigurationSettings.speechToTextHttpEndpoint.value
    } else {
        "${ConfigurationSettings.httpServerEndpoint.value}${HttpClientPath.SpeechToText}"
    },
    val intentRecognitionHttpEndpoint: String = if (ConfigurationSettings.isUseCustomIntentRecognitionHttpEndpoint.value) {
        ConfigurationSettings.intentRecognitionHttpEndpoint.value
    } else {
        "${ConfigurationSettings.httpServerEndpoint.value}${HttpClientPath.TextToIntent}"
    },
    val isHandleIntentDirectly: Boolean = ConfigurationSettings.intentHandlingOption.value == IntentHandlingOptions.WithRecognition,
    val textToSpeechHttpEndpoint: String = if (ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value) {
        ConfigurationSettings.speechToTextHttpEndpoint.value
    } else {
        "${ConfigurationSettings.httpServerEndpoint.value}${HttpClientPath.TextToSpeech}"
    },
    val audioPlayingHttpEndpoint: String = ConfigurationSettings.audioPlayingHttpEndpoint.value,
    val intentHandlingHttpEndpoint: String = ConfigurationSettings.intentHandlingHttpEndpoint.value,
    val intentHandlingHassEndpoint: String = ConfigurationSettings.intentHandlingHassEndpoint.value,
    val intentHandlingHassAccessToken: String = ConfigurationSettings.intentHandlingHassAccessToken.value
)