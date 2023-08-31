package org.rhasspy.mobile.logic.connections.httpclient

import org.rhasspy.mobile.data.service.option.IntentHandlingOption

internal data class HttpClientServiceParams(
    val siteId: String,
    val isHttpSSLVerificationDisabled: Boolean,
    val httpClientServerEndpointHost: String,
    val httpClientServerEndpointPort: Int,
    val httpClientTimeout: Long?,
    val speechToTextHttpEndpoint: String,
    val intentRecognitionHttpEndpoint: String,
    val textToSpeechHttpEndpoint: String,
    val audioPlayingHttpEndpoint: String,
    val intentHandlingHttpEndpoint: String,
    val intentHandlingHomeAssistantEndpoint: String,
    val intentHandlingHomeAssistantAccessToken: String,
    val intentHandlingOption: IntentHandlingOption
)