package org.rhasspy.mobile.services.httpclient

data class HttpClientParams(
    val isHttpSSLVerificationDisabled: Boolean,
    val speechToTextHttpEndpoint: String,
    val intentRecognitionHttpEndpoint: String,
    val isHandleIntentDirectly: Boolean,
    val textToSpeechHttpEndpoint: String,
    val audioPlayingHttpEndpoint: String,
    val intentHandlingHttpEndpoint: String,
    val intentHandlingHassEndpoint: String,
    val intentHandlingHassAccessToken: String,
)